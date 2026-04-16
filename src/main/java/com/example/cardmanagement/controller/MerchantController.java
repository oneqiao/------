package com.example.cardmanagement.controller;

import com.example.cardmanagement.entity.Merchant;
import com.example.cardmanagement.service.MerchantService;
import com.example.cardmanagement.util.PageUtil;
import com.example.cardmanagement.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 商户控制器
 * 处理商户管理相关的接口
 */
@RestController
@RequestMapping("/api/merchants")
public class MerchantController {
    
    @Autowired
    private MerchantService merchantService;
    
    /**
     * 分页查询商户列表
     * @param page 页码
     * @param size 每页条数
     * @param merchantNo 商户号
     * @param name 商户名称
     * @param merchantType 商户类型
     * @param agentId 代理ID
     * @param accountStatus 账号状态
     * @param fundFreeze 资金冻结
     * @param negativeBalance 余额负数
     * @return Response 商户列表
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
            // 创建分页请求
            Pageable pageable = PageUtil.createPageable(page, size);
            
            // 查询商户列表
            var merchants = merchantService.getMerchantList(pageable, merchantNo, name, merchantType, 
                                                          agentId, accountStatus, fundFreeze, negativeBalance);
            
            // 构建分页响应
            var response = PageUtil.buildPageResponse(merchants);
            return Response.success(response);
        } catch (Exception e) {
            return Response.error(500, "查询商户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取商户详情
     * @param id 商户ID
     * @return Response 商户详情
     */
    @GetMapping("/{id}")
    public Response getMerchantDetail(@PathVariable Long id) {
        try {
            var detail = merchantService.getMerchantDetail(id);
            return Response.success(detail);
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "获取商户详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 新增商户
     * @param merchant 商户对象
     * @return Response 新增结果
     */
    @PostMapping
    public Response addMerchant(@RequestBody Merchant merchant) {
        try {
            Merchant savedMerchant = merchantService.addMerchant(merchant);
            return Response.success("新增商户成功", savedMerchant);
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "新增商户失败: " + e.getMessage());
        }
    }
    
    /**
     * 编辑商户
     * @param id 商户ID
     * @param merchant 商户对象
     * @return Response 编辑结果
     */
    @PutMapping("/{id}")
    public Response updateMerchant(@PathVariable Long id, @RequestBody Merchant merchant) {
        try {
            Merchant updatedMerchant = merchantService.updateMerchant(id, merchant);
            return Response.success("编辑商户成功", updatedMerchant);
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "编辑商户失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除商户（软删除）
     * @param id 商户ID
     * @return Response 删除结果
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
     * 调整商户余额
     * @param id 商户ID
     * @param balanceData 余额调整数据，包含type、amount和reason
     * @return Response 调整结果
     */
    @PostMapping("/{id}/balance")
    public Response adjustBalance(@PathVariable Long id, @RequestBody Map<String, Object> balanceData) {
        try {
            String type = (String) balanceData.get("type");
            BigDecimal amount = new BigDecimal(balanceData.get("amount").toString());
            String reason = (String) balanceData.get("reason");
            
            var result = merchantService.adjustBalance(id, type, amount, reason);
            return Response.success("调整成功", result);
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "调整余额失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出商户列表为Excel
     * @param merchantNo 商户号
     * @param name 商户名称
     * @param merchantType 商户类型
     * @param agentId 代理ID
     * @param accountStatus 账号状态
     * @param fundFreeze 资金冻结
     * @param negativeBalance 余额负数
     * @return ResponseEntity<byte[]> Excel文件
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
            // 导出商户列表
            byte[] excelData = merchantService.exportMerchants(merchantNo, name, merchantType, agentId, 
                                                             accountStatus, fundFreeze, negativeBalance);
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "merchants_" + System.currentTimeMillis() + ".xlsx");
            headers.setContentLength(excelData.length);
            
            return ResponseEntity.ok().headers(headers).body(excelData);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}