package com.example.cardmanagement.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.example.cardmanagement.entity.Merchant;
import com.example.cardmanagement.enums.MerchantType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 导出工具类
 * 用于处理商户列表的Excel导出
 */
public class ExportUtil {

    /**
     * 导出商户列表为Excel
     * @param merchants 商户列表
     * @return byte[] Excel文件字节数组
     * @throws IOException IO异常
     */
    public static byte[] exportMerchantsToExcel(List<Merchant> merchants) throws IOException {
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();

        // 创建工作表
        Sheet sheet = workbook.createSheet("商户列表");

        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"商户号", "商户名称", "商户类型", "代理名称", "登录账号", "账号状态", "资金冻结", "当前余额", "开卡数", "卡内余额", "创建时间"};

        // 设置表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);

        // 填充表头
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

        // 设置数据行样式
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 填充数据
        for (int i = 0; i < merchants.size(); i++) {
            Merchant merchant = merchants.get(i);
            Row dataRow = sheet.createRow(i + 1);

            // 商户号
            dataRow.createCell(0).setCellValue(merchant.getMerchantNo());
            // 商户名称
            dataRow.createCell(1).setCellValue(merchant.getName());
            // 商户类型（转换为字符串）
            dataRow.createCell(2).setCellValue(merchant.getMerchantType().getDescription());  // 使用 getDescription() 获取类型描述
            // 代理名称
            dataRow.createCell(3).setCellValue(merchant.getAgent() != null ? merchant.getAgent().getName() : "");
            // 登录账号
            dataRow.createCell(4).setCellValue(merchant.getLoginAccount());
            // 账号状态
            String accountStatusText = "";
            if (merchant.getAccountStatus() != null) {
                switch (merchant.getAccountStatus()) {
                    case 0: accountStatusText = "禁用";
                        break;
                    case 1: accountStatusText = "正常";
                        break;
                    case 2: accountStatusText = "删除";
                        break;
                }
            }
            dataRow.createCell(5).setCellValue(accountStatusText);
            // 资金冻结
            dataRow.createCell(6).setCellValue(merchant.getFundFreeze() != null && merchant.getFundFreeze() ? "冻结" : "未冻结");
            // 当前余额
            dataRow.createCell(7).setCellValue(merchant.getCurrentBalance() != null ? merchant.getCurrentBalance().toString() : "0.00");
            // 开卡数
            dataRow.createCell(8).setCellValue(merchant.getCardCount() != null ? merchant.getCardCount() : 0);
            // 卡内余额
            dataRow.createCell(9).setCellValue(merchant.getCardBalance() != null ? merchant.getCardBalance().toString() : "0.00");
            // 创建时间
            dataRow.createCell(10).setCellValue(merchant.getCreateTime() != null ? merchant.getCreateTime().toString() : "");

            // 设置数据行样式
            for (int j = 0; j < headers.length; j++) {
                dataRow.getCell(j).setCellStyle(dataStyle);
            }
        }

        // 调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 输出到字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}