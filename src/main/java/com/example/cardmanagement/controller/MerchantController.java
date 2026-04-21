package com.example.cardmanagement.controller;

import com.example.cardmanagement.dto.MerchantDTO;
import com.example.cardmanagement.service.MerchantService;
import com.example.cardmanagement.util.PageUtil;
import com.example.cardmanagement.util.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 商户管理控制器。
 */
@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    /**
     * 分页查询商户列表。
     * 默认不返回已软删除的商户。
     */
    @GetMapping
    public Response getMerchantList(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String merchantNo,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) String merchantType,
                                    @RequestParam(required = false) Long agentId,
                                    @RequestParam(required = false) Integer accountStatus,
                                    @RequestParam(required = false) Integer fundFreeze,
                                    @RequestParam(required = false) Integer negativeBalance) {
        try {
            Pageable pageable = PageUtil.createPageable(page, size);
            var merchants = merchantService.getMerchantList(
                    pageable, merchantNo, name, merchantType, agentId, accountStatus, fundFreeze, negativeBalance
            );
            return Response.success(PageUtil.buildPageResponse(merchants));
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "查询商户列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取商户详情。
     */
    @GetMapping("/{id}")
    public Response getMerchantDetail(@PathVariable Long id) {
        try {
            return Response.success(merchantService.getMerchantDetail(id));
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "获取商户详情失败: " + e.getMessage());
        }
    }

    /**
     * 新增商户。
     */
    @PostMapping
    public Response addMerchant(@RequestBody MerchantDTO merchantDTO) {
        if (merchantDTO == null) {
            return Response.error(400, "请求体不能为空");
        }

        try {
            return Response.success("新增商户成功", merchantService.addMerchant(merchantDTO));
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "新增商户失败: " + e.getMessage());
        }
    }

    /**
     * 编辑商户。
     */
    @PutMapping("/{id}")
    public Response updateMerchant(@PathVariable Long id, @RequestBody MerchantDTO merchantDTO) {
        if (merchantDTO == null) {
            return Response.error(400, "请求体不能为空");
        }

        try {
            return Response.success("编辑商户成功", merchantService.updateMerchant(id, merchantDTO));
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "编辑商户失败: " + e.getMessage());
        }
    }

    /**
     * 软删除商户。
     */
    @DeleteMapping("/{id}")
    public Response deleteMerchant(@PathVariable Long id) {
        try {
            merchantService.deleteMerchant(id);
            return Response.success("删除商户成功");
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "删除商户失败: " + e.getMessage());
        }
    }

    /**
     * 调整商户余额。
     */
    @PostMapping("/{id}/balance")
    public Response adjustBalance(@PathVariable Long id, @RequestBody Map<String, Object> balanceData) {
        if (balanceData == null) {
            return Response.error(400, "请求体不能为空");
        }

        try {
            Object typeObj = balanceData.get("type");
            Object amountObj = balanceData.get("amount");
            Object reasonObj = balanceData.get("reason");

            if (typeObj == null || amountObj == null) {
                return Response.error(400, "type 和 amount 不能为空");
            }

            String type = String.valueOf(typeObj);
            BigDecimal amount = new BigDecimal(String.valueOf(amountObj));
            String reason = reasonObj == null ? "" : String.valueOf(reasonObj);

            return Response.success("调整成功", merchantService.adjustBalance(id, type, amount, reason));
        } catch (NumberFormatException e) {
            return Response.error(400, "amount 格式不正确");
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "调整余额失败: " + e.getMessage());
        }
    }

    /**
     * 导出商户列表为 Excel。
     * 默认不导出已软删除的商户。
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportMerchants(@RequestParam(required = false) String merchantNo,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String merchantType,
                                                  @RequestParam(required = false) Long agentId,
                                                  @RequestParam(required = false) Integer accountStatus,
                                                  @RequestParam(required = false) Integer fundFreeze,
                                                  @RequestParam(required = false) Integer negativeBalance) {
        try {
            byte[] excelData = merchantService.exportMerchants(
                    merchantNo, name, merchantType, agentId, accountStatus, fundFreeze, negativeBalance
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                    MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            );
            headers.setContentDispositionFormData(
                    "attachment", "merchants_" + System.currentTimeMillis() + ".xlsx"
            );
            headers.setContentLength(excelData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
