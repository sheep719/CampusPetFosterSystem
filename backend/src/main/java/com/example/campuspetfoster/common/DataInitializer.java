package com.example.campuspetfoster.common;

import com.example.campuspetfoster.entity.*;
import com.example.campuspetfoster.mapper.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PetOwnerProfileMapper petOwnerProfileMapper;
    private final PetCaregiverProfileMapper petCaregiverProfileMapper;
    private final PetMapper petMapper;
    private final FosterLocationMapper fosterLocationMapper;
    private final FosterApplicationMapper fosterApplicationMapper;
    private final CareDiaryMapper careDiaryMapper;
    private final HandoverRecordMapper handoverRecordMapper;
    private final NotificationMapper notificationMapper;
    private final PasswordEncoder passwordEncoder;

    private static final int TARGET_COUNT = 200;
    private static final Random random = new Random();

    private static final String[] PET_SPECIES = {"cat", "dog", "rabbit", "hamster", "bird"};
    private static final String[] CAT_BREEDS = {"英短", "布偶", "橘猫", "暹罗", "美短", "波斯", "狸花猫", "加菲"};
    private static final String[] DOG_BREEDS = {"金毛", "泰迪", "哈士奇", "柯基", "柴犬", "拉布拉多", "萨摩耶", "边牧"};
    private static final String[] RABBIT_BREEDS = {"垂耳兔", "荷兰兔", "狮子兔", "安哥拉兔"};
    private static final String[] HAMSTER_BREEDS = {"金丝熊", "布丁", "银狐", "紫仓"};
    private static final String[] BIRD_BREEDS = {"虎皮鹦鹉", "牡丹鹦鹉", "玄凤", "画眉"};
    private static final String[] GENDERS = {"male", "female"};
    private static final String[] VACCINE_STATUS = {"已接种", "未接种", "部分接种"};
    private static final String[] DEWORM_STATUS = {"已驱虫", "未驱虫"};
    private static final String[] CARE_NOTES = {"每天喂两次", "每天遛弯", "温顺", "活泼", "需要梳毛", "怕生"};
    private static final String[] REVIEW_TEXTS = {"照顾得很好", "满意", "还不错", "非常棒", "狗狗很开心"};
    private static final String[] SCHOOLS = {"XX大学", "YY大学", "ZZ大学"};
    private static final String[] MAJORS = {"计算机科学与技术", "软件工程", "人工智能", "电子信息", "通信工程", "自动化"};
    private static final String[] CARE_TYPES = "[\"cat\"]", "[\"dog\"]", "[\"cat\",\"dog\"]", "[\"rabbit\"]", "[\"cat\",\"rabbit\"]";

    public DataInitializer(SysUserMapper sysUserMapper,
                          PetOwnerProfileMapper petOwnerProfileMapper,
                          PetCaregiverProfileMapper petCaregiverProfileMapper,
                          PetMapper petMapper,
                          FosterLocationMapper fosterLocationMapper,
                          FosterApplicationMapper fosterApplicationMapper,
                          CareDiaryMapper careDiaryMapper,
                          HandoverRecordMapper handoverRecordMapper,
                          NotificationMapper notificationMapper,
                          PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.petOwnerProfileMapper = petOwnerProfileMapper;
        this.petCaregiverProfileMapper = petCaregiverProfileMapper;
        this.petMapper = petMapper;
        this.fosterLocationMapper = fosterLocationMapper;
        this.fosterApplicationMapper = fosterApplicationMapper;
        this.careDiaryMapper = careDiaryMapper;
        this.handoverRecordMapper = handoverRecordMapper;
        this.notificationMapper = notificationMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String encodedPassword = passwordEncoder.encode("123456");
        updateDefaultPasswords(encodedPassword);
        generateTestData();
    }

    private void updateDefaultPasswords(String encodedPassword) {
        SysUser admin = sysUserMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "admin"));
        if (admin != null) {
            admin.setPassword(encodedPassword);
            sysUserMapper.updateById(admin);
        }
        System.out.println("=== 默认密码更新完成 ===");
    }

    private void generateTestData() {
        int existingUsers = sysUserMapper.selectCount(null);
        int existingOwners = petOwnerProfileMapper.selectCount(null);
        int existingCaregivers = petCaregiverProfileMapper.selectCount(null);
        int existingPets = petMapper.selectCount(null);
        int existingLocations = fosterLocationMapper.selectCount(null);
        int existingApplications = fosterApplicationMapper.selectCount(null);
        int existingDiaries = careDiaryMapper.selectCount(null);
        int existingHandovers = handoverRecordMapper.selectCount(null);
        int existingNotifications = notificationMapper.selectCount(null);

        System.out.println("=== 当前数据量 ===");
        System.out.println("sys_user: " + existingUsers);
        System.out.println("pet_owner_profile: " + existingOwners);
        System.out.println("pet_caregiver_profile: " + existingCaregivers);
        System.out.println("pet: " + existingPets);
        System.out.println("foster_location: " + existingLocations);
        System.out.println("foster_application: " + existingApplications);
        System.out.println("care_diary: " + existingDiaries);
        System.out.println("handover_record: " + existingHandovers);
        System.out.println("notification: " + existingNotifications);

        if (existingUsers < TARGET_COUNT) {
            generateUsers(TARGET_COUNT - existingUsers, existingUsers);
        }
        if (existingOwners < TARGET_COUNT) {
            generateOwners(TARGET_COUNT - existingOwners, existingOwners);
        }
        if (existingCaregivers < TARGET_COUNT) {
            generateCaregivers(TARGET_COUNT - existingCaregivers, existingCaregivers);
        }
        if (existingPets < TARGET_COUNT) {
            generatePets(TARGET_COUNT - existingPets, existingPets);
        }
        if (existingLocations < TARGET_COUNT) {
            generateLocations(TARGET_COUNT - existingLocations, existingLocations);
        }
        if (existingApplications < TARGET_COUNT) {
            generateApplications(TARGET_COUNT - existingApplications, existingApplications);
        }
        if (existingDiaries < TARGET_COUNT) {
            generateDiaries(TARGET_COUNT - existingDiaries, existingDiaries);
        }
        if (existingHandovers < TARGET_COUNT) {
            generateHandovers(TARGET_COUNT - existingHandovers, existingHandovers);
        }
        if (existingNotifications < TARGET_COUNT) {
            generateNotifications(TARGET_COUNT - existingNotifications, existingNotifications);
        }

        System.out.println("=== 测试数据生成完成 ===");
    }

    private void generateUsers(int count, int startIndex) {
        String encodedPassword = passwordEncoder.encode("123456");
        for (int i = 0; i < count; i++) {
            int idx = startIndex + i + 1;
            SysUser user = new SysUser();
            String roleCode = random.nextBoolean() ? "owner" : "caregiver";
            user.setUsername(roleCode.equals("owner") ? "student" + String.format("%03d", idx) : "caregiver" + String.format("%03d", idx));
            user.setPassword(encodedPassword);
            user.setRoleCode(roleCode);
            user.setPhone("13" + (random.nextInt(9) + 1) + String.format("%08d", random.nextInt(100000000)));
            user.setEnabled(1);
            user.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(90)));
            sysUserMapper.insert(user);
        }
        System.out.println("生成 " + count + " 条 sys_user 数据");
    }

    private void generateOwners(int count, int startIndex) {
        for (int i = 0; i < count; i++) {
            int idx = startIndex + i + 1;
            PetOwnerProfile owner = new PetOwnerProfile();
            owner.setUserId((long) (idx + 1));
            owner.setStudentNo("2024" + String.format("%03d", idx));
            owner.setName(generateChineseName());
            owner.setSchool(SCHOOLS[random.nextInt(SCHOOLS.length)]);
            owner.setMajor(MAJORS[random.nextInt(MAJORS.length)]);
            owner.setDorm((random.nextBoolean() ? "男生" : "女生") + "宿舍" + (random.nextInt(10) + 1) + "号楼" + (random.nextInt(60) + 101));
            owner.setContactPhone("13" + (random.nextInt(9) + 1) + String.format("%08d", random.nextInt(100000000)));
            petOwnerProfileMapper.insert(owner);
        }
        System.out.println("生成 " + count + " 条 pet_owner_profile 数据");
    }

    private void generateCaregivers(int count, int startIndex) {
        String[] caregiverNames = {"李阿姨", "王阿姨", "张叔叔", "陈阿姨", "刘阿姨", "赵阿姨", "孙叔叔", "周阿姨"};
        String[] addresses = {"XX小区", "幸福小区", "阳光花园", "锦绣家园", "明珠小区", "绿地花园"};
        for (int i = 0; i < count; i++) {
            int idx = startIndex + i + 1;
            PetCaregiverProfile caregiver = new PetCaregiverProfile();
            caregiver.setUserId((long) (idx + 1));
            caregiver.setName(caregiverNames[random.nextInt(caregiverNames.length)] + idx);
            caregiver.setAddress(addresses[random.nextInt(addresses.length)] + (random.nextInt(10) + 1) + "号楼" + (random.nextInt(50) + 101) + "室");
            caregiver.setDistanceToSchoolKm(Math.round(random.nextDouble() * 5 + 0.5) * 1.0);
            caregiver.setPhone("13" + (random.nextInt(9) + 1) + String.format("%08d", random.nextInt(100000000)));
            caregiver.setExperienceYears(random.nextInt(10) + 1);
            caregiver.setHasOwnPets(random.nextBoolean() ? 1 : 0);
            caregiver.setCareTypes(CARE_TYPES[random.nextInt(CARE_TYPES.length)]);
            caregiver.setBio("喜欢小动物，有丰富的饲养经验");
            caregiver.setAvgRating(Math.round((random.nextDouble() * 1.5 + 3.5) * 10) / 10.0);
            caregiver.setTotalCareCount(random.nextInt(50));
            petCaregiverProfileMapper.insert(caregiver);
        }
        System.out.println("生成 " + count + " 条 pet_caregiver_profile 数据");
    }

    private void generatePets(int count, int startIndex) {
        String[] petNames = {"咪咪", "旺财", "花花", "小黑", "小白", "橘子", "布丁", "可乐", "薯条", "雪球", "豆豆", "团团", "芒果", "西瓜"};
        for (int i = 0; i < count; i++) {
            int idx = startIndex + i + 1;
            Pet pet = new Pet();
            pet.setOwnerId((long) (random.nextInt(8) + 2));
            pet.setPetName(petNames[random.nextInt(petNames.length)] + idx);
            String species = PET_SPECIES[random.nextInt(PET_SPECIES.length)];
            pet.setSpecies(species);
            pet.setBreed(getBreedBySpecies(species));
            pet.setGender(GENDERS[random.nextInt(GENDERS.length)]);
            pet.setAge(random.nextInt(10) + 1);
            pet.setHealthNote("健康");
            pet.setDietNote("专用粮");
            pet.setVaccineStatus(VACCINE_STATUS[random.nextInt(VACCINE_STATUS.length)]);
            pet.setDewormStatus(DEWORM_STATUS[random.nextInt(DEWORM_STATUS.length)]);
            petMapper.insert(pet);
        }
        System.out.println("生成 " + count + " 条 pet 数据");
    }

    private void generateLocations(int count, int startIndex) {
        String[] locationNames = {"宠物之家", "爱心小院", "快乐窝", "温馨家园", "萌宠乐园"};
        for (int i = 0; i < count; i++) {
            int idx = startIndex + i + 1;
            FosterLocation location = new FosterLocation();
            location.setCaregiverId((long) (random.nextInt(5) + 3));
            location.setLocationName(locationNames[random.nextInt(locationNames.length)] + idx);
            location.setAddress("XX市XX区" + (random.nextInt(10) + 1) + "路" + (random.nextInt(100) + 1) + "号");
            location.setDistanceKm(Math.round(random.nextDouble() * 5 + 0.5) * 1.0);
            location.setCapacity(random.nextInt(5) + 1);
            location.setAcceptedSpecies(CARE_TYPES[random.nextInt(CARE_TYPES.length)]);
            location.setCarePeriod("全天");
            location.setCostDesc((random.nextInt(50) + 10) + "元/天");
            location.setAvailable(1);
            fosterLocationMapper.insert(location);
        }
        System.out.println("生成 " + count + " 条 foster_location 数据");
    }

    private void generateApplications(int count, int startIndex) {
        String[] statuses = {"pending", "in_progress", "completed"};
        for (int i = 0; i < count; i++) {
            int idx = startIndex + i + 1;
            FosterApplication app = new FosterApplication();
            app.setPetId((long) (random.nextInt(15) + 1));
            app.setLocationId((long) (random.nextInt(5) + 1));
            app.setCaregiverId((long) (random.nextInt(5) + 3));
            app.setOwnerId((long) (random.nextInt(8) + 2));
            LocalDateTime start = LocalDateTime.now().minusDays(random.nextInt(30));
            app.setStartTime(start);
            app.setExpectedEndTime(start.plusDays(random.nextInt(14) + 1));
            String status = statuses[random.nextInt(statuses.length)];
            app.setStatus(status);
            if ("fostering".equals(status)) {
                app.setActualStartTime(start);
            } else if ("completed".equals(status)) {
                app.setActualStartTime(start);
                app.setActualEndTime(start.plusDays(random.nextInt(7) + 1));
                app.setRating(random.nextInt(5) + 1);
                app.setReviewText(REVIEW_TEXTS[random.nextInt(REVIEW_TEXTS.length)]);
            }
            app.setCareNote(CARE_NOTES[random.nextInt(CARE_NOTES.length)]);
            app.setEmergencyContact("13" + (random.nextInt(9) + 1) + String.format("%08d", random.nextInt(100000000)));
            app.setCreateTime(start.minusDays(1));
            fosterApplicationMapper.insert(app);
        }
        System.out.println("生成 " + count + " 条 foster_application 数据");
    }

    private void generateDiaries(int count, int startIndex) {
        for (int i = 0; i < count; i++) {
            int idx = startIndex + i + 1;
            CareDiary diary = new CareDiary();
            diary.setApplicationId((long) (random.nextInt(15) + 1));
            diary.setCaregiverId((long) (random.nextInt(5) + 3));
            diary.setDiaryDate(LocalDate.now().minusDays(random.nextInt(14)));
            diary.setContent("宠物状态良好，" + CARE_NOTES[random.nextInt(CARE_NOTES.length)]);
            careDiaryMapper.insert(diary);
        }
        System.out.println("生成 " + count + " 条 care_diary 数据");
    }

    private void generateHandovers(int count, int startIndex) {
        String[] types = {"pickup", "return"};
        for (int i = 0; i < count; i++) {
            int idx = startIndex + i + 1;
            HandoverRecord record = new HandoverRecord();
            record.setApplicationId((long) (random.nextInt(15) + 1));
            record.setHandoverType(types[random.nextInt(types.length)]);
            record.setHandoverTime(LocalDateTime.now().minusDays(random.nextInt(30)));
            record.setFromPerson(generateChineseName());
            record.setToPerson(generateChineseName());
            record.setRemark("交接正常");
            handoverRecordMapper.insert(record);
        }
        System.out.println("生成 " + count + " 条 handover_record 数据");
    }

    private void generateNotifications(int count, int startIndex) {
        String[] types = {"system", "application", "reminder"};
        String[] titles = {"系统通知", "新申请", "寄养提醒", "审核结果"};
        String[] contents = {"系统已更新", "收到新的寄养申请", "寄养即将开始", "申请已通过", "申请已拒绝"};
        for (int i = 0; i < count; i++) {
            int idx = startIndex + i + 1;
            Notification notification = new Notification();
            notification.setUserId((long) (random.nextInt(14) + 1));
            notification.setType(types[random.nextInt(types.length)]);
            notification.setTitle(titles[random.nextInt(titles.length)]);
            notification.setContent(contents[random.nextInt(contents.length)]);
            notification.setLinkUrl("/");
            notification.setReadStatus(random.nextBoolean());
            notification.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(7)));
            if (notification.getReadStatus()) {
                notification.setReadTime(notification.getCreateTime().plusHours(random.nextInt(48)));
            }
            notificationMapper.insert(notification);
        }
        System.out.println("生成 " + count + " 条 notification 数据");
    }

    private String getBreedBySpecies(String species) {
        switch (species) {
            case "cat": return CAT_BREEDS[random.nextInt(CAT_BREEDS.length)];
            case "dog": return DOG_BREEDS[random.nextInt(DOG_BREEDS.length)];
            case "rabbit": return RABBIT_BREEDS[random.nextInt(RABBIT_BREEDS.length)];
            case "hamster": return HAMSTER_BREEDS[random.nextInt(HAMSTER_BREEDS.length)];
            case "bird": return BIRD_BREEDS[random.nextInt(BIRD_BREEDS.length)];
            default: return "其他";
        }
    }

    private String generateChineseName() {
        String[] surnames = {"张", "李", "王", "陈", "刘", "赵", "孙", "周", "吴", "郑"};
        String[] names = {"伟", "强", "敏", "静", "磊", "军", "洋", "勇", "艳", "杰"};
        return surnames[random.nextInt(surnames.length)] + names[random.nextInt(names.length)] + names[random.nextInt(names.length)];
    }
}
