package com.example.campuspetfoster.common;

import com.example.campuspetfoster.entity.FosterApplication;
import com.example.campuspetfoster.entity.Pet;
import com.example.campuspetfoster.mapper.FosterApplicationMapper;
import com.example.campuspetfoster.mapper.PetMapper;
import com.example.campuspetfoster.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExpireReminderTask {

    private final FosterApplicationMapper fosterApplicationMapper;
    private final PetMapper petMapper;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void checkExpiringApplications() {
        List<FosterApplication> applications = fosterApplicationMapper.selectList(null);
        LocalDateTime now = LocalDateTime.now();

        for (FosterApplication app : applications) {
            if ("confirmed".equals(app.getStatus()) && app.getExpectedEndTime() != null) {
                long hoursRemaining = ChronoUnit.HOURS.between(now, app.getExpectedEndTime());
                long daysRemaining = ChronoUnit.DAYS.between(now, app.getExpectedEndTime());

                Pet pet = petMapper.selectById(app.getPetId());
                String petName = pet != null ? pet.getPetName() : "宠物";

                if (hoursRemaining <= 24 && hoursRemaining > 0) {
                    sendReminder(app, "寄养即将到期提醒",
                            String.format("您的宠物 %s 的寄养将于 %s 到期，请及时安排接回。",
                                    petName, app.getExpectedEndTime()),
                            hoursRemaining, "24h");
                } else if (daysRemaining <= 3 && daysRemaining > 0) {
                    sendReminder(app, "寄养到期提醒",
                            String.format("您的宠物 %s 的寄养还有 %d 天到期，请提前安排接回。",
                                    petName, daysRemaining),
                            daysRemaining, "3d");
                }
            }

            if ("completed".equals(app.getStatus()) && app.getActualEndTime() != null) {
                long daysPast = ChronoUnit.DAYS.between(app.getActualEndTime(), now);
                if (daysPast == 1 && app.getRating() == null) {
                    Pet pet = petMapper.selectById(app.getPetId());
                    String petName = pet != null ? pet.getPetName() : "宠物";
                    notificationService.createNotification(app.getOwnerId(), "rating",
                            "请评价寄养服务",
                            String.format("您的宠物 %s 已完成寄养，请对本次服务进行评价。", petName),
                            "/owner-applications");
                }
            }
        }
    }

    private void sendReminder(FosterApplication app, String title, String content, long remaining, String threshold) {
        notificationService.createNotification(app.getOwnerId(), "expire", title, content, "/owner-applications");
        notificationService.createNotification(app.getCaregiverId(), "expire", title, content, "/caregiver-applications");
    }
}
