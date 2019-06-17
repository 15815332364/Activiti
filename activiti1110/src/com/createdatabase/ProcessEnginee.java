package com.createdatabase;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.TaskQuery;
import org.junit.Test;

public class ProcessEnginee {
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/**
	 * 部署流程定义（操作表：act_re_deployment、act_re_procdef、act_ge_bytearray）
	 */
	@Test
	public void test4() {
		// 获取一个部署构建器对象，用于加载流程定义文件（test1.bpmn,test.png）完成流程定义部署
		DeploymentBuilder builder = processEngine.getRepositoryService().createDeployment();
		builder.addClasspathResource("test1.bpmn");// 文件转为二进制存到act_ge_bytearray表
		builder.addClasspathResource("test1.png");
		// 部署流程定义
		Deployment deployment = builder.deploy();
		System.out.println(deployment.getId());

	}

	/**
	 * 查询流程定义列表(操作表：act_re_procdef)
	 */
	@Test
	public void test5() {
		// 流程定义查询对象
		ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();
		// 添加过滤条件
		query.processDefinitionKey("qjlc");
		// 添加排序条件
		query.orderByProcessDefinitionVersion().desc();
		// 添加分页查询
		query.listPage(0, 10);
		List<ProcessDefinition> list = query.list();
		for (ProcessDefinition processDefinition : list) {
			System.out.println(processDefinition.getId());
		}
	}

	/**
	 * 根据Id启动流程实例（操作表：act_ru_execution）
	 */
	@Test
	public void test6() {
		String processDefinitionId = "qjlc:4:304";
		ProcessInstance processInstance = processEngine.getRuntimeService()
				.startProcessInstanceById(processDefinitionId);
		System.out.println(processInstance.getId());
	}

	/**
	 * 查询个人任务列表（操作表：act_ru_execution）
	 */
	@Test
	public void test7() {
		TaskQuery query = processEngine.getTaskService().createTaskQuery();
		String assignee = "王五";
		query.taskAssignee(assignee);
		List<org.activiti.engine.task.Task> list = query.list();
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).getId() + " " + list.get(i).getName());
		}
	}

	/**
	 * 办理任务（操作：act_hi_taskinst）
	 */
	@Test
	public void test8() {
		String taskId = "902";
		processEngine.getTaskService().complete(taskId);
	}
}
