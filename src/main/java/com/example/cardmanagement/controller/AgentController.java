package com.example.cardmanagement.controller;

import com.example.cardmanagement.entity.Agent;
import com.example.cardmanagement.service.AgentService;
import com.example.cardmanagement.util.PageUtil;
import com.example.cardmanagement.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * 代理控制器
 * 处理代理管理相关的接口
 */
@RestController
@RequestMapping("/api/agents")
public class AgentController {
    
    @Autowired
    private AgentService agentService;
    
    /**
     * 分页查询代理列表
     * @param page 页码
     * @param size 每页条数
     * @param name 代理名称
     * @param status 状态
     * @return Response 代理列表
     */
    @GetMapping
    public Response getAgentList(@RequestParam(defaultValue = "1") int page, 
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String name, 
                              @RequestParam(required = false) Integer status) {
        try {
            // 创建分页请求
            Pageable pageable = PageUtil.createPageable(page, size);
            
            // 查询代理列表
            var agents = agentService.getAgentList(pageable, name, status);
            
            // 构建分页响应
            var response = PageUtil.buildPageResponse(agents);
            return Response.success(response);
        } catch (Exception e) {
            return Response.error(500, "查询代理列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 新增代理
     * @param agent 代理对象
     * @return Response 新增结果
     */
    @PostMapping
    public Response addAgent(@RequestBody Agent agent) {
        try {
            Agent savedAgent = agentService.addAgent(agent);
            return Response.success("新增代理成功", savedAgent);
        } catch (Exception e) {
            return Response.error(500, "新增代理失败: " + e.getMessage());
        }
    }
    
    /**
     * 编辑代理
     * @param id 代理ID
     * @param agent 代理对象
     * @return Response 编辑结果
     */
    @PutMapping("/{id}")
    public Response updateAgent(@PathVariable Long id, @RequestBody Agent agent) {
        try {
            Agent updatedAgent = agentService.updateAgent(id, agent);
            return Response.success("编辑代理成功", updatedAgent);
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "编辑代理失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除代理
     * @param id 代理ID
     * @return Response 删除结果
     */
    @DeleteMapping("/{id}")
    public Response deleteAgent(@PathVariable Long id) {
        try {
            agentService.deleteAgent(id);
            return Response.success("删除代理成功");
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "删除代理失败: " + e.getMessage());
        }
    }
}