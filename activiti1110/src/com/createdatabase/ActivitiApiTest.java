package com.createdatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * 使用activiti框架的API操作流程
 * 
 * @author Administrator
 *
 */
public class ActivitiApiTest {
	ProcessEngine processEnginee = ProcessEngines.getDefaultProcessEngine();

	/**
	 * 部署流程定义 方式一：读取单个的流程定义文件 方式二：读取zip压缩文件
	 */
	@Test
	public void test1() {
		DeploymentBuilder builder = processEnginee.getRepositoryService().createDeployment();
		// 方式一：读取单个的流程定义文件
		// builder.addClasspathResource("test1.bpmn");
		// builder.addClasspathResource("test1.png");
		// builder.deploy();
		// 方式二：读取zip压缩文件
		ZipInputStream zipInputStream = new ZipInputStream(
				this.getClass().getClassLoader().getResourceAsStream("process.zip"));
		builder.addZipInputStream(zipInputStream);
		builder.deploy();
	}

	/**
	 * 查询部署列表
	 */
	@Test
	public void test2() {
		DeploymentQuery query = processEnginee.getRepositoryService().createDeploymentQuery();
		List<Deployment> list = query.list();
		for (Deployment deployment : list) {
			String id = deployment.getId();
			System.out.println(id);
		}
	}

	/**
	 * 查询流程定义列表
	 */
	@Test
	public void test3() {
		// 流程定义查询对象，查询表：act_re_procdef
		ProcessDefinitionQuery query = processEnginee.getRepositoryService().createProcessDefinitionQuery();
		List<ProcessDefinition> list = query.list();
		for (int i = 0; i < list.size(); i++) {
			System.err.println(list.get(i).getName());
		}
	}

	/**
	 * 删除部署信息
	 */
	public void test4() {
		String deploymentId = "1";
		// processEnginee.getRepositoryService().deleteDeployment(deploymentId);
		processEnginee.getRepositoryService().deleteDeployment(deploymentId, true);// 是否级联删除
	}

	/**
	 * 删除流程定义(通过删除部署信息达到删除流程定义的目的)
	 */
	@Test
	public void test5() {
		String deploymentId = "1401";
		// processEngine.getRepositoryService().deleteDeployment(deploymentId);
		processEnginee.getRepositoryService().deleteDeployment(deploymentId, true);
	}

	/**
	 * 查询一次部署对应的流程文件和对应的输入流（bpmn png）
	 * 
	 * @throws IOException
	 */
	@Test
	public void test6() throws IOException {
		String deploymentId = "101";
		List<String> names = processEnginee.getRepositoryService().getDeploymentResourceNames(deploymentId);
		for (String name : names) {
			System.out.println(name);
			InputStream inputStream = processEnginee.getRepositoryService().getResourceAsStream(deploymentId, name);
			// 将文件保存到本地磁盘
			OutputStream outputStream = new FileOutputStream(new File("d:\\" + name));
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(b)) != -1) {
				outputStream.write(b);
			}
			outputStream.close();
			inputStream.close();
			// FileUtils.copyInputStreamToFile(inputStream, new File("d:\\" +
			// name));
			// inputStream.close();
		}
	}

	/**
	 * 获取png图片的输入流
	 * 
	 * @throws IOException
	 */
	@Test
	public void test7() throws IOException {
		String deploymentId = "101";
		InputStream pngInputStream = processEnginee.getRepositoryService().getProcessDiagram(deploymentId);
		FileUtils.copyInputStreamToFile(pngInputStream, new File("d:\\my.png"));
	}

	/**
	 * 启动流程实例 方式一：根据流程id启动 方式二：根据流程定义的key启动（自动选择最新版本的流程定义启动流程实例）
	 */
	@Test
	public void test8() {
		// String processDefinitionId = "qjlc:3:204";
		// ProcessInstance instance =
		// processEnginee.getRuntimeService().startProcessInstanceById(processDefinitionId);
		// System.out.println(instance.getId());
		String processDefinitionKey = "qjlc";
		ProcessInstance instance = processEnginee.getRuntimeService().startProcessInstanceByKey(processDefinitionKey);
		System.out.println(instance.getId());
	}

	/**
	 * 查询流程实例列表
	 */
	@Test
	public void test9() {
		// 流程实例查询对象，查询act_ru_execution
		ProcessInstanceQuery query = processEnginee.getRuntimeService().createProcessInstanceQuery();
		query.processDefinitionKey("qjlc");
		query.orderByProcessDefinitionKey().desc();
		List<ProcessInstance> list = query.list();
		for (ProcessInstance instance : list) {
			System.out.println(instance.getId() + " " + instance.getActivityId());
		}
	}

	/**
	 * 结束流程实例
	 */
	@Test
	public void test10() {
		String processInstanceId = "1601";
		processEnginee.getRuntimeService().deleteProcessInstance(processInstanceId, "我愿意");
	}

	/**
	 * 查询任务列表
	 */
	@Test
	public void test11() {
		// 任务查询对象,查询act_ru_task表
		TaskQuery query = processEnginee.getTaskService().createTaskQuery();
		String assignee = "李四";
		query.taskAssignee(assignee);
		query.orderByTaskCreateTime().desc();
		List<Task> list = query.list();
		for (Task task : list) {
			System.out.println(task.getId());
		}
	}

	/**
	 * 办理任务
	 */
	@Test
	public void test12() {
		String taskId = "2902";
		processEnginee.getTaskService().complete(taskId);
	}

	/**
	 * 直接将流程向下执行一步
	 */
	@Test
	public void test13() {
		String executionId = "2701";// 流程实例id
		processEnginee.getRuntimeService().signal(executionId);
	}

	/**
	 * 查询最新版本的流程定义列表
	 */
	@Test
	public void test14() {
		ProcessDefinitionQuery query = processEnginee.getRepositoryService().createProcessDefinitionQuery();
		query.orderByProcessDefinitionVersion().asc();
		List<ProcessDefinition> list = query.list();
		Map<String, ProcessDefinition> map = new HashMap<String, ProcessDefinition>();
		for (ProcessDefinition pd : list) {
			map.put(pd.getKey(), pd);
		}
		ArrayList<ProcessDefinition> lastList = new ArrayList<>(map.values());
		for (ProcessDefinition processDefinition : lastList) {
			System.out.println(processDefinition.getName() + "  " + processDefinition.getVersion());
		}
	}
}
