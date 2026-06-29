package com.example.campuspetfoster.controller;

import com.example.campuspetfoster.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/dicts")
public class DictController {

    /**
     * 获取字典类型列表
     */
    @GetMapping("/types")
    public Result<List<Map<String, Object>>> getDictTypeList() {
        List<Map<String, Object>> dictTypes = new ArrayList<>();

        // 宠物种类
        Map<String, Object> petSpecies = new HashMap<>();
        petSpecies.put("dictCode", "pet_species");
        petSpecies.put("dictName", "宠物种类");
        petSpecies.put("description", "系统中登记的宠物种类分类");
        dictTypes.add(petSpecies);

        // 宠物性别
        Map<String, Object> petGender = new HashMap<>();
        petGender.put("dictCode", "pet_gender");
        petGender.put("dictName", "宠物性别");
        petGender.put("description", "宠物的性别分类");
        dictTypes.add(petGender);

        // 疫苗状态
        Map<String, Object> vaccineStatus = new HashMap<>();
        vaccineStatus.put("dictCode", "vaccine_status");
        vaccineStatus.put("dictName", "疫苗状态");
        vaccineStatus.put("description", "宠物的疫苗接种状态");
        dictTypes.add(vaccineStatus);

        // 驱虫状态
        Map<String, Object> dewormStatus = new HashMap<>();
        dewormStatus.put("dictCode", "deworm_status");
        dewormStatus.put("dictName", "驱虫状态");
        dewormStatus.put("description", "宠物的驱虫状态");
        dictTypes.add(dewormStatus);

        // 寄养点类型
        Map<String, Object> locationType = new HashMap<>();
        locationType.put("dictCode", "location_type");
        locationType.put("dictName", "寄养点类型");
        locationType.put("description", "寄养点的类型分类");
        dictTypes.add(locationType);

        // 寄养申请状态
        Map<String, Object> applicationStatus = new HashMap<>();
        applicationStatus.put("dictCode", "application_status");
        applicationStatus.put("dictName", "寄养申请状态");
        applicationStatus.put("description", "寄养申请的各种状态");
        dictTypes.add(applicationStatus);

        // 交接类型
        Map<String, Object> handoverType = new HashMap<>();
        handoverType.put("dictCode", "handover_type");
        handoverType.put("dictName", "交接类型");
        handoverType.put("description", "交接记录的类型分类");
        dictTypes.add(handoverType);

        // 通知类型
        Map<String, Object> notificationType = new HashMap<>();
        notificationType.put("dictCode", "notification_type");
        notificationType.put("dictName", "通知类型");
        notificationType.put("description", "系统通知的类型分类");
        dictTypes.add(notificationType);

        return Result.success(dictTypes);
    }

    /**
     * 根据字典类型获取字典数据
     */
    @GetMapping("/{dictCode}")
    public Result<List<Map<String, Object>>> getDictDataByCode(@PathVariable String dictCode) {
        List<Map<String, Object>> dictData = getDictData(dictCode);
        return Result.success(dictData);
    }

    /**
     * 获取字典类型详情
     */
    @GetMapping("/types/{dictCode}")
    public Result<Map<String, Object>> getDictTypeByCode(@PathVariable String dictCode) {
        Map<String, Object> dictType = new HashMap<>();
        dictType.put("dictCode", dictCode);
        dictType.put("dictName", getDictName(dictCode));
        dictType.put("description", getDictDescription(dictCode));
        dictType.put("data", getDictData(dictCode));

        return Result.success(dictType);
    }

    private String getDictName(String dictCode) {
        switch (dictCode) {
            case "pet_species": return "宠物种类";
            case "pet_gender": return "宠物性别";
            case "vaccine_status": return "疫苗状态";
            case "deworm_status": return "驱虫状态";
            case "location_type": return "寄养点类型";
            case "application_status": return "寄养申请状态";
            case "handover_type": return "交接类型";
            case "notification_type": return "通知类型";
            default: return "未知字典类型";
        }
    }

    private String getDictDescription(String dictCode) {
        switch (dictCode) {
            case "pet_species": return "系统中登记的宠物种类分类";
            case "pet_gender": return "宠物的性别分类";
            case "vaccine_status": return "宠物的疫苗接种状态";
            case "deworm_status": return "宠物的驱虫状态";
            case "location_type": return "寄养点的类型分类";
            case "application_status": return "寄养申请的各种状态";
            case "handover_type": return "交接记录的类型分类";
            case "notification_type": return "系统通知的类型分类";
            default: return "未知字典类型";
        }
    }

    private List<Map<String, Object>> getDictData(String dictCode) {
        List<Map<String, Object>> data = new ArrayList<>();

        switch (dictCode) {
            case "pet_species":
                data.add(createDictData("cat", "猫", "1"));
                data.add(createDictData("dog", "狗", "2"));
                data.add(createDictData("bird", "鸟类", "3"));
                data.add(createDictData("rabbit", "兔子", "4"));
                data.add(createDictData("hamster", "仓鼠", "5"));
                data.add(createDictData("other", "其他", "6"));
                break;

            case "pet_gender":
                data.add(createDictData("male", "公", "1"));
                data.add(createDictData("female", "母", "2"));
                break;

            case "vaccine_status":
                data.add(createDictData("completed", "已完成", "1"));
                data.add(createDictData("partial", "部分完成", "2"));
                data.add(createDictData("not_started", "未开始", "3"));
                data.add(createDictData("unknown", "未知", "4"));
                break;

            case "deworm_status":
                data.add(createDictData("completed", "已完成", "1"));
                data.add(createDictData("not_started", "未开始", "2"));
                data.add(createDictData("unknown", "未知", "3"));
                break;

            case "location_type":
                data.add(createDictData("dormitory", "宿舍", "1"));
                data.add(createDictData("apartment", "校外公寓", "2"));
                data.add(createDictData("house", "住宅", "3"));
                data.add(createDictData("other", "其他", "4"));
                break;

            case "application_status":
                data.add(createDictData("pending", "待审核", "1", "待管理员审核"));
                data.add(createDictData("approved", "已通过", "2", "审核通过，等待寄养"));
                data.add(createDictData("rejected", "已拒绝", "3", "管理员审核未通过"));
                data.add(createDictData("in_progress", "进行中", "4", "宠物正在寄养中"));
                data.add(createDictData("completed", "已完成", "5", "寄养完成，宠物已归还"));
                data.add(createDictData("cancelled", "已取消", "6", "用户取消申请"));
                break;

            case "handover_type":
                data.add(createDictData("delivery", "送达", "1", "宠物送达寄养点"));
                data.add(createDictData("return", "归还", "2", "宠物归还给主人"));
                data.add(createDictData("visit", "中途探视", "3", "主人中途探视宠物"));
                break;

            case "notification_type":
                data.add(createDictData("application", "申请通知", "1", "寄养申请相关通知"));
                data.add(createDictData("approval", "审核通知", "2", "审核结果通知"));
                data.add(createDictData("handover", "交接通知", "3", "交接相关通知"));
                data.add(createDictData("reminder", "提醒通知", "4", "到期提醒通知"));
                data.add(createDictData("system", "系统通知", "5", "系统公告通知"));
                break;
        }

        return data;
    }

    private Map<String, Object> createDictData(String value, String label, String sortOrder) {
        return createDictData(value, label, sortOrder, null);
    }

    private Map<String, Object> createDictData(String value, String label, String sortOrder, String remark) {
        Map<String, Object> dictData = new HashMap<>();
        dictData.put("value", value);
        dictData.put("label", label);
        dictData.put("sortOrder", sortOrder);
        dictData.put("remark", remark);
        return dictData;
    }
}