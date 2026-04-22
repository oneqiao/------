package com.example.cardmanagement.controller;

import com.example.cardmanagement.dto.AgentDTO;
import com.example.cardmanagement.service.AgentService;
import com.example.cardmanagement.util.PageUtil;
import com.example.cardmanagement.util.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * 代理控制器。
 */
@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    /**
     * 分页查询代理列表。
     */
    @GetMapping
    public Response getAgentList(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String name,
                                 @RequestParam(required = false) Integer status) {
        try {
            Pageable pageable = PageUtil.createPageable(page, size);
            var agents = agentService.getAgentList(pageable, name, status);
            return Response.success(PageUtil.buildPageResponse(agents));
        } catch (Exception e) {
            return Response.error(500, "查询代理列表失败: " + e.getMessage());
        }
    }

    /**
     * 新增代理。
     */
    @PostMapping
    public Response addAgent(@RequestBody AgentDTO agentDTO) {
        if (agentDTO == null) {
            return Response.error(400, "请求体不能为空");
        }

        try {
            return Response.success("新增代理成功", agentService.addAgent(agentDTO));
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "新增代理失败: " + e.getMessage());
        }
    }

    /**
     * 编辑代理。
     */
    @PutMapping("/{id}")
    public Response updateAgent(@PathVariable Long id, @RequestBody AgentDTO agentDTO) {
        if (agentDTO == null) {
            return Response.error(400, "请求体不能为空");
        }

        try {
            return Response.success("编辑代理成功", agentService.updateAgent(id, agentDTO));
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "编辑代理失败: " + e.getMessage());
        }
    }

    /**
     * 删除代理。
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
