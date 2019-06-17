package com.createdatabase;

import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.junit.Test;

public class CreateDatabase {
	/**
	 * 使用框架提供的自动建表（不提供配置文件）
	 */
	@Test
	public void test1() {
		// 创建流程引擎
		ProcessEngineConfiguration conf = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
		// 设置数据库信息
		conf.setJdbcDriver("com.mysql.jdbc.Driver");
		conf.setJdbcUrl("jdbc:mysql://localhost:3306/activiti110_web");
		conf.setJdbcUsername("fqh");
		conf.setJdbcPassword("123456");
		// 设置自动建表
		conf.setDatabaseSchemaUpdate("true");
		conf.buildProcessEngine();
	}

	/**
	 * 使用配置文件建表
	 */
	@Test
	public void test2() {
		String resource = "activiti-context.xml";
		String beanName = "processEngineConfiguration";
		ProcessEngineConfiguration configuration = ProcessEngineConfiguration
				.createProcessEngineConfigurationFromResource(resource, beanName);
		configuration.buildProcessEngine();
	}

	/**
	 * 使用框架自动建表（使用默认配置）
	 */
	@Test
	public void test3() {
		ProcessEngines.getDefaultProcessEngine();
	}
}
