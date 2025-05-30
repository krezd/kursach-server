package com.example.kursachserver.service;

import com.example.kursachserver.dto.response.UserRatingDto;
import com.example.kursachserver.enumModel.Severity;
import com.example.kursachserver.model.User;
import com.example.kursachserver.repository.UserRepository;
import com.example.kursachserver.repository.ViolationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Service
public class RatingService {
    private final ViolationRepository violationRepo;
    private final UserRepository userRepo;

    private static final Map<Severity, Integer> SEVERITY_POINTS = Map.of(
            Severity.LOW, 1,
            Severity.MEDIUM, 3,
            Severity.HIGH, 5,
            Severity.CRITICAL, 8
    );

    @Autowired
    public RatingService(ViolationRepository violationRepo,
                         UserRepository userRepo) {
        this.violationRepo = violationRepo;
        this.userRepo = userRepo;
    }

    private static final String FONT_PATH = "C:/Windows/Fonts/arial.ttf";


    public byte[] generateRatingReport(
            OffsetDateTime from,
            OffsetDateTime to
    ) throws Exception {
        List<UserRatingDto> ratings = getUserRatings(
                from, to);

        return buildPdf(
                "Рейтинг опасности пользователей",
                from, to,
                ratings
        );
    }

    public byte[] generateUserRatingReport(
            Long userId,
            OffsetDateTime from,
            OffsetDateTime to
    ) throws Exception {
        UserRatingDto r = getUserRating(
                userId, from, to);

        return buildPdf(
                "Рейтинг опасности пользователя",
                from, to,
                List.of(r)
        );
    }

    // Общая сборка PDF
    private byte[] buildPdf(
            String titleText,
            OffsetDateTime from,
            OffsetDateTime to,
            List<UserRatingDto> data
    ) throws Exception {
        Document doc = new Document(PageSize.A4.rotate(), 36, 36, 54, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, baos);
        doc.open();

        // Шрифты
        BaseFont bf = BaseFont.createFont(
                FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFont = new Font(bf, 18, Font.BOLD);
        Font normalFont = new Font(bf, 12, Font.NORMAL);
        Font smallFont = new Font(bf, 10, Font.NORMAL);

        // Заголовок
        Paragraph title = new Paragraph(titleText, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);
        doc.add(Chunk.NEWLINE);

        // Блок фильтров
        PdfPTable info = new PdfPTable(2);
        info.setWidthPercentage(60);
        info.addCell(cell("Период:", normalFont, false));
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        info.addCell(cell(
                df.format(from) + " — " + df.format(to),
                normalFont, false
        ));
        doc.add(info);
        doc.add(Chunk.NEWLINE);

        // Таблица
        PdfPTable table = new PdfPTable(new float[]{3, 3, 2});
        table.setWidthPercentage(100);
        // Шапка
        String[] hdr = {"Пользователь", "Должность", "Рейтинг"};
        for (String h : hdr) {
            PdfPCell c = new PdfPCell(new Phrase(h, new Font(bf, 11, Font.BOLD, Color.WHITE)));
            c.setBackgroundColor(new Color(0, 123, 255));
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c);
        }
        // Данные
        for (UserRatingDto u : data) {
            table.addCell(new PdfPCell(new Phrase(u.getName(), smallFont)));
            table.addCell(new PdfPCell(new Phrase(u.getPosition(), smallFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(u.getScore()), smallFont)));
        }
        doc.add(table);

        doc.close();
        return baos.toByteArray();
    }

    private PdfPCell cell(String text, Font f, boolean withBorder) {
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        if (!withBorder) c.setBorder(Rectangle.NO_BORDER);
        return c;
    }

    public List<UserRatingDto> getUserRatings(OffsetDateTime from, OffsetDateTime to) {
        List<ViolationRepository.UserSeverityCount> raw =
                violationRepo.countByUserAndSeverity(from, to);

        Map<Long, Long> scores = new HashMap<>();
        for (var rec : raw) {
            long uid = rec.getUserId();
            int pts = SEVERITY_POINTS.getOrDefault(rec.getSeverity(), 0);
            long count = rec.getCnt();
            scores.merge(uid, pts * count, Long::sum);
        }

        List<UserRatingDto> result = new ArrayList<>();
        for (var entry : scores.entrySet()) {
            User u = userRepo.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalStateException("No user " + entry.getKey()));
            result.add(new UserRatingDto(u.getId(),
                    u.getName(),
                    u.getPosition(),
                    entry.getValue()));
        }
        result.sort(Comparator.comparingLong(UserRatingDto::getScore).reversed());
        return result;
    }

    public UserRatingDto getUserRating(Long userId, OffsetDateTime from, OffsetDateTime to) {
        List<UserRatingDto> all = getUserRatings(from, to);
        return all.stream()
                .filter(r -> r.getUserId().equals(userId))
                .findFirst()
                .orElse(new UserRatingDto(userId, "—", "—", 0L));
    }
}