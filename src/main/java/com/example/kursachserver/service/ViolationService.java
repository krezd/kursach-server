package com.example.kursachserver.service;

import com.example.kursachserver.dto.response.ViolationDto;
import com.example.kursachserver.enumModel.Severity;
import com.example.kursachserver.enumModel.ViolationType;
import com.example.kursachserver.model.User;
import com.example.kursachserver.model.Violation;
import com.example.kursachserver.repository.ViolationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class ViolationService {
    private final ViolationRepository repo;
    private final SimpMessagingTemplate ws;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public ViolationService(ViolationRepository repo,
                            SimpMessagingTemplate ws) {
        this.repo = repo;
        this.ws = ws;
    }

    @Transactional
    public ViolationDto record(
            User user,
            ViolationType type,
            Severity severity,
            Map<String, Object> meta  // передаём любые дополнительные поля
    ) {
        Violation v = new Violation();
        v.setUser(user);
        v.setType(type);
        v.setSeverity(severity);
        v.setOccurredAt(OffsetDateTime.now());
        String jsonMeta;
        try {
            jsonMeta = mapper.writeValueAsString(meta);
        } catch (JsonProcessingException e) {
            jsonMeta = "{}";
        }
        v.setMetadata(jsonMeta);
        ViolationDto saved = new ViolationDto(repo.save(v));

        ws.convertAndSend("/topic/violations", saved);
        return saved;
    }

    public List<Violation> filter(Long userId, String type, String severity, String from, String to) {
        List<Violation> all = repo.findAll();
        Stream<Violation> stream = all.stream();

        if (userId != null)
            stream = stream.filter(v -> v.getUser() != null && v.getUser().getId().equals(userId));
        if (type != null)
            stream = stream.filter(v -> v.getType().name().equalsIgnoreCase(type));
        if (severity != null)
            stream = stream.filter(v -> v.getSeverity().name().equalsIgnoreCase(severity));
        if (from != null)
            stream = stream.filter(v -> v.getOccurredAt().isAfter(OffsetDateTime.from(Instant.parse(from))));
        if (to != null)
            stream = stream.filter(v -> v.getOccurredAt().isBefore(OffsetDateTime.from(Instant.parse(to))));

        return stream.toList();
    }

    public byte[] generateReport(
            Long userId,
            ViolationType type,
            Severity severity,
            OffsetDateTime from,
            OffsetDateTime to
    ) throws Exception {

        // 1. Получаем данные
        List<Violation> violations = repo.findAllFiltered(userId, type, severity, from, to);

        // 2. Настраиваем PDF
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        // 3. Подключаем шрифт с кириллицей
        String fontPath = "C:/Windows/Fonts/arial.ttf"; // или другой путь к TTF
        BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFont  = new Font(bf, 18, Font.BOLD);
        Font normalFont = new Font(bf, 12, Font.NORMAL);
        Font smallFont  = new Font(bf, 10, Font.NORMAL);

        // 4. Заголовок
        Paragraph title = new Paragraph("Отчёт по нарушениям", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // 5. Блок фильтров
        PdfPTable info = new PdfPTable(2);
        info.setWidthPercentage(60);
        info.setHorizontalAlignment(Element.ALIGN_LEFT);
        info.addCell(new PdfPCell(new Phrase("Пользователь:", normalFont)) {{ setBorder(Rectangle.NO_BORDER); }});
        info.addCell(new PdfPCell(new Phrase(userId != null ? violations.getFirst().getUser().getName(): "Все", normalFont)) {{ setBorder(Rectangle.NO_BORDER); }});
        info.addCell(new PdfPCell(new Phrase("Тип нарушения:", normalFont)) {{ setBorder(Rectangle.NO_BORDER); }});
        info.addCell(new PdfPCell(new Phrase(type != null ? type.name() : "Все", normalFont)) {{ setBorder(Rectangle.NO_BORDER); }});
        info.addCell(new PdfPCell(new Phrase("Степень серьёзности:", normalFont)) {{ setBorder(Rectangle.NO_BORDER); }});
        info.addCell(new PdfPCell(new Phrase(severity != null ? severity.name() : "Все", normalFont)) {{ setBorder(Rectangle.NO_BORDER); }});
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        info.addCell(new PdfPCell(new Phrase("Период:", normalFont)) {{ setBorder(Rectangle.NO_BORDER); }});
        info.addCell(new PdfPCell(new Phrase(
                (from != null ? dtf.format(from) : "—") + " — " + (to != null ? dtf.format(to) : "—"),
                normalFont
        )) {{ setBorder(Rectangle.NO_BORDER); }});
        document.add(info);
        document.add(Chunk.NEWLINE);

        // 6. Создаём таблицу с данными
        PdfPTable table = new PdfPTable(new float[]{2, 3, 3, 2, 2, 6});
        table.setWidthPercentage(100);
        // Заголовки
        String[] headers = { "Дата", "Пользователь", "Должность", "Тип", "Степень", "Детали" };
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, normalFont));
            cell.setBackgroundColor(new Color(0, 123, 255));
            cell.setPhrase(new Phrase(h, new Font(bf, 11, Font.BOLD, Color.WHITE)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Данные
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        for (Violation v : violations) {
            table.addCell(new PdfPCell(new Phrase(fmt.format(v.getOccurredAt()), smallFont)));
            table.addCell(new PdfPCell(new Phrase(v.getUser().getName(), smallFont)));
            table.addCell(new PdfPCell(new Phrase(v.getUser().getPosition(), smallFont)));
            table.addCell(new PdfPCell(new Phrase(v.getType().name(), smallFont)));
            table.addCell(new PdfPCell(new Phrase(v.getSeverity().name(), smallFont)));
            table.addCell(new PdfPCell(new Phrase(parseMeta(v.getMetadata()), smallFont)));
        }

        document.add(table);
        document.close();
        return baos.toByteArray();
    }


    private com.lowagie.text.pdf.PdfPCell createHeaderCell(String text, Font font, Color bg) {
        com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    // Мини-парсер metadata
    private String parseMeta(String json) {
        if (json == null || json.equals("{}")) return "";
        // Можно использовать org.json, Jackson, Gson и т.д.
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> map = mapper.readValue(json, java.util.Map.class);
            return map.entrySet().stream()
                    .map(e -> e.getKey() + ": " + e.getValue())
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("");
        } catch (Exception ex) {
            return json;
        }
    }
}
