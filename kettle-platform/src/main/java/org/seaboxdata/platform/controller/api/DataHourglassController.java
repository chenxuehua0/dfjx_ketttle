package org.seaboxdata.platform.controller.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.ibatis.session.SqlSession;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.DefaultLogLevel;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositorySecurityProvider;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.seaboxdata.ext.App;
import org.seaboxdata.ext.PluginFactory;
import org.seaboxdata.ext.TransExecutor;
import org.seaboxdata.ext.base.GraphCodec;
import org.seaboxdata.ext.utils.StringEscapeHelper;
import org.seaboxdata.systemmng.entity.DatabaseConnEntity;
import org.seaboxdata.systemmng.entity.TaskGroupAttributeEntity;
import org.seaboxdata.systemmng.service.system.CommonService;
import org.seaboxdata.systemmng.utils.auth.PropertiesUtil;
import org.seaboxdata.systemmng.utils.task.CarteClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * @see 数据沙漏api接口
 * 
 * @author zhaozm
 *
 */
@RestController
@RequestMapping(value = "/api/dataHourglass")
public class DataHourglassController {

    @Autowired
    protected CommonService cService;
	
    private final static Logger logger = LoggerFactory.getLogger(DataHourglassController.class);
	
	@Value("${jdbc.username}")
	private String username;
	@Value("${jdbc.password}")
	private String password;

	/**
	 * @see 初始化转换
	 * @param params
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/initTrans")
	protected void initTrans(@RequestParam String params) throws Exception {
		logger.info("调用接口参数：{}", params);
		//params = "[{\"transName\":\"测试\",\"databases\":{\"from\":{\"databaseName\":\"kettle\",\"type\":\"MYSQL\",\"hostname\":\"localhost\",\"password\":\"123456\",\"port\":\"3306\",\"name\":\"kettle1\",\"username\":\"root\"},\"to\":{\"databaseName\":\"testdb\",\"type\":\"POSTGRESQL\",\"hostname\":\"10.1.3.155\",\"password\":\"123456\",\"port\":\"5432\",\"name\":\"postgresql\",\"username\":\"test\"}},\"steps\":{\"InsertUpdate\":{\"schema\":\"testdb\",\"updateFields\":\"[{\\\"update\\\":\\\"Y\\\",\\\"updateLookup\\\":\\\"id\\\",\\\"updateStream\\\":\\\"id\\\"},{\\\"update\\\":\\\"Y\\\",\\\"updateLookup\\\":\\\"NAME\\\",\\\"updateStream\\\":\\\"NAME\\\"},{\\\"update\\\":\\\"Y\\\",\\\"updateLookup\\\":\\\"ALIAS\\\",\\\"updateStream\\\":\\\"ALIAS\\\"},{\\\"update\\\":\\\"Y\\\",\\\"updateLookup\\\":\\\"SN\\\",\\\"updateStream\\\":\\\"SN\\\"},{\\\"update\\\":\\\"Y\\\",\\\"updateLookup\\\":\\\"ICON\\\",\\\"updateStream\\\":\\\"ICON\\\"},{\\\"update\\\":\\\"Y\\\",\\\"updateLookup\\\":\\\"cls\\\",\\\"updateStream\\\":\\\"cls\\\"},{\\\"update\\\":\\\"Y\\\",\\\"updateLookup\\\":\\\"RESOURCE_TYPE\\\",\\\"updateStream\\\":\\\"RESOURCE_TYPE\\\"},{\\\"update\\\":\\\"Y\\\",\\\"updateLookup\\\":\\\"PARENT_ID\\\",\\\"updateStream\\\":\\\"PARENT_ID\\\"},{\\\"update\\\":\\\"Y\\\",\\\"updateLookup\\\":\\\"DEFAULT_URL\\\",\\\"updateStream\\\":\\\"DEFAULT_URL\\\"},{\\\"update\\\":\\\"Y\\\",\\\"updateLookup\\\":\\\"IS_OPEN\\\",\\\"updateStream\\\":\\\"IS_OPEN\\\"},{\\\"update\\\":\\\"Y\\\",\\\"updateLookup\\\":\\\"DESC\\\",\\\"updateStream\\\":\\\"DESC\\\"}]\",\"connection\":\"postgresql\",\"searchFields\":\"[{\\\"keyCondition\\\":\\\"=\\\",\\\"keyLookup\\\":\\\"id\\\",\\\"keyStream2\\\":\\\"\\\",\\\"keyStream1\\\":\\\"id\\\"}]\",\"table\":\"sys_resource\"},\"TableInput\":{\"sql\":\"SELECT id, NAME, ALIAS, SN, ICON, cls, RESOURCE_TYPE, PARENT_ID, DEFAULT_URL, IS_OPEN, `DESC` FROM sys_resource\",\"connection\":\"kettle1\"},\"TableOutput\":{\"schema\":\"testdb\",\"connection\":\"postgresql\",\"fields\":\"[{\\\"column_name\\\":\\\"id\\\",\\\"stream_name\\\":\\\"id\\\"},{\\\"column_name\\\":\\\"NAME\\\",\\\"stream_name\\\":\\\"NAME\\\"},{\\\"column_name\\\":\\\"ALIAS\\\",\\\"stream_name\\\":\\\"ALIAS\\\"},{\\\"column_name\\\":\\\"SN\\\",\\\"stream_name\\\":\\\"SN\\\"},{\\\"column_name\\\":\\\"ICON\\\",\\\"stream_name\\\":\\\"ICON\\\"},{\\\"column_name\\\":\\\"cls\\\",\\\"stream_name\\\":\\\"cls\\\"},{\\\"column_name\\\":\\\"RESOURCE_TYPE\\\",\\\"stream_name\\\":\\\"RESOURCE_TYPE\\\"},{\\\"column_name\\\":\\\"PARENT_ID\\\",\\\"stream_name\\\":\\\"PARENT_ID\\\"},{\\\"column_name\\\":\\\"DEFAULT_URL\\\",\\\"stream_name\\\":\\\"DEFAULT_URL\\\"},{\\\"column_name\\\":\\\"IS_OPEN\\\",\\\"stream_name\\\":\\\"IS_OPEN\\\"},{\\\"column_name\\\":\\\"DESC\\\",\\\"stream_name\\\":\\\"DESC\\\"}]\",\"table\":\"sys_resource\"}}}]";

		//参数转换
		JSONArray array = JSONUtil.parseArray(params);
		List<Map> list = JSONUtil.toList(array, Map.class);
		
		//循环处理参数
		for (Map map : list) {
			this.doInitTrans(map) ;
		}
		
	}

	/**
	 * @see 处理转换
	 * @param map
	 * @throws Exception
	 */
	protected void doInitTrans(Map map) throws Exception {
		logger.info("循环处理转换参数：{}", map);
		//转换名称
		String transName = map.get("transName").toString();
		//创建转换
		this.createTrans(transName);
		
		String parseXmlStr = initXml(transName, map);
		
		this.saveSteps(parseXmlStr);
		
		this.runTrans(parseXmlStr);
	}
	
	/**
	 * @see 运行转换
	 * @param graphXml
	 * @throws Exception
	 */
	protected void runTrans(String graphXml) throws Exception {
		TransExecutionConfiguration executionConfiguration = initRun(graphXml);
		//执行ID
		String executionId = run(graphXml ,executionConfiguration);
		//this.result(executionId);
	}
	
    /**
     * @see 初始化运行参数
     * @param graphXml
     * @return
     * @throws Exception
     */
    protected TransExecutionConfiguration initRun(String graphXml) throws Exception {
		GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
        TransMeta transMeta = (TransMeta) codec.decode(graphXml);
        transMeta.setRepository(App.getInstance().getRepository());
        transMeta.setMetaStore(App.getInstance().getMetaStore());

        TransExecutionConfiguration executionConfiguration = App.getInstance().getTransExecutionConfiguration();

        if (transMeta.findFirstUsedClusterSchema() != null) {
            executionConfiguration.setExecutingLocally(false);
            executionConfiguration.setExecutingRemotely(false);
            executionConfiguration.setExecutingClustered(true);
        } else {
            executionConfiguration.setExecutingLocally(true);
            executionConfiguration.setExecutingRemotely(false);
            executionConfiguration.setExecutingClustered(false);
        }

        // Remember the variables set previously
        //
        RowMetaAndData variables = App.getInstance().getVariables();
        Object[] data = variables.getData();
        String[] fields = variables.getRowMeta().getFieldNames();
        Map<String, String> variableMap = new HashMap<String, String>();
        for (int idx = 0; idx < fields.length; idx++) {
            variableMap.put(fields[idx], data[idx].toString());
        }

        executionConfiguration.setVariables(variableMap);
        executionConfiguration.getUsedVariables(transMeta);
        executionConfiguration.getUsedArguments(transMeta, App.getInstance().getArguments());
        executionConfiguration.setReplayDate(null);
        executionConfiguration.setRepository(App.getInstance().getRepository());
        executionConfiguration.setSafeModeEnabled(false);

        executionConfiguration.setLogLevel(DefaultLogLevel.getLogLevel());

        // Fill the parameters, maybe do this in another place?
        Map<String, String> params = executionConfiguration.getParams();
        params.clear();
        String[] paramNames = transMeta.listParameters();
        for (String name : paramNames) {
            params.put(name, "");
        }
        
        return executionConfiguration;
	}
	
	/**
	 * @see 运行转换
	 * @param graphXml
	 * @param transExecutionConfiguration
	 * @return
	 * @throws Exception
	 */
    protected String run(String graphXml, TransExecutionConfiguration transExecutionConfiguration) throws Exception {
        GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
        
        TransMeta transMeta = (TransMeta) codec.decode(graphXml);

        TransExecutor transExecutor = TransExecutor.initExecutor(transExecutionConfiguration, transMeta);
        Thread tr = new Thread(transExecutor, "TransExecutor_" + transExecutor.getExecutionId());
        tr.start();

        return transExecutor.getExecutionId();
    }	

	/**
	 * @see 运行结果
	 * @param executionId
	 * @throws Exception
	 */
    protected void result(String executionId) throws Exception {
        JSONObject jsonObject = JSONUtil.createObj();
        TransExecutor transExecutor = TransExecutor.getExecutor(executionId);
        if (transExecutor != null) {
            jsonObject.putOpt("finished", transExecutor.isFinished());
            if (transExecutor.isFinished()) {
                TransExecutor.remove(executionId);
                jsonObject.putOpt("stepMeasure", transExecutor.getStepMeasure());
                jsonObject.putOpt("log", transExecutor.getExecutionLog());
                jsonObject.putOpt("stepStatus", transExecutor.getStepStatus());
            } else {
                jsonObject.putOpt("stepMeasure", transExecutor.getStepMeasure());
                jsonObject.putOpt("log", transExecutor.getExecutionLog());
                jsonObject.putOpt("stepStatus", transExecutor.getStepStatus());
            }
        }
        
        response(jsonObject);
    }	
    
    private static ThreadLocal<HttpServletResponse> tl = new ThreadLocal<HttpServletResponse>();
    
    /**
     * @see 返回结果
     * @param jsonObject
     * @throws IOException
     */
	public void response(JSONObject jsonObject) throws IOException {
		HttpServletResponse response = tl.get();
		response.setContentType("text/html; charset=utf-8");
		response.getWriter().write(jsonObject.toString());
	}    
    
	/**
	 * @see 创建转换
	 * 
	 * @param transName
	 * @throws KettleException
	 * @throws IOException
	 */
	protected void createTrans(String transName) throws KettleException, IOException {
		boolean isSuccess = false;
		Repository repository = App.getInstance().getRepository();
		if (repository == null) {
			repository.init(App.getInstance().meta);
			repository.connect(username, password);
		}
		RepositoryDirectoryInterface directory = null;
		TransMeta transMeta = null;
		SqlSession sqlSession = CarteClient.sessionFactory.openSession();
		
		try {
			directory = repository.findDirectory("/");
			if (directory == null) {
				directory = repository.getUserHomeDirectory();
			}

			// 转换名称

			transMeta = new TransMeta();
			transMeta.setRepository(App.getInstance().getRepository());
			transMeta.setMetaStore(App.getInstance().getMetaStore());
			transMeta.setName(transName);
			transMeta.setRepositoryDirectory(directory);
			transMeta.setCreatedUser("1");// 沙箱用户ID
			repository.save(transMeta, "add: " + new Date(), null);
			isSuccess = true;

			// 添加任务组记录
			Integer taskId = Integer.valueOf(transMeta.getObjectId().getId());
			TaskGroupAttributeEntity attr = new TaskGroupAttributeEntity();
			attr.setTaskGroupName("P");// taskGroupName 数据沙箱 用户组
			attr.setType("trans");
			attr.setTaskId(taskId);
			attr.setTaskPath("/" + transName);
			attr.setTaskName(transName);
			sqlSession.insert("org.seaboxdata.systemmng.dao.TaskGroupDao.addTaskGroupAttribute", attr);
			sqlSession.commit();
			sqlSession.close();

			String transPath = directory.getPath();
			if (!transPath.endsWith("/")) {
				transPath = transPath + '/';
			}
			transPath = transPath + transName;
		} catch (Exception e) {
			// 出现异常回滚
			logger.error("保存转换异常：{}", e.getMessage());
			if (e instanceof KettleException) {
				repository.disconnect();
				repository.init(App.getInstance().meta);
				repository.connect(username, password);
			}
			sqlSession.rollback();
			sqlSession.close();
			// 删除转换
			if (isSuccess) {
				ObjectId id = repository.getTransformationID(transName, directory);
				repository.deleteTransformation(id);
			}
		}
	}

	/**
	 * @see 保存Steps
	 * @param graphXml
	 * @throws Exception
	 */
	protected void saveSteps(String graphXml) throws Exception {
		Repository repository = null;
		GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
		String xml = StringEscapeHelper.decode(graphXml);

		AbstractMeta transMeta = codec.decode(xml);
		repository = App.getInstance().getRepository();
		ObjectId existingId = repository.getTransformationID(transMeta.getName(), transMeta.getRepositoryDirectory());
		if (transMeta.getCreatedDate() == null) {
			transMeta.setCreatedDate(new Date());
		}

		if (transMeta.getObjectId() == null) {
			transMeta.setObjectId(existingId);
		}

		// 沙箱用户ID
		if (StringUtils.isEmpty(transMeta.getCreatedUser())) {
			transMeta.setCreatedUser("1");
			transMeta.setModifiedUser("1");
		} else {
			transMeta.setModifiedUser("1");
		}

		transMeta.setModifiedDate(new Date());

		boolean versioningEnabled = true;
		boolean versionCommentsEnabled = true;
		String fullPath = transMeta.getRepositoryDirectory() + "/" + transMeta.getName()
				+ transMeta.getRepositoryElementType().getExtension();
		RepositorySecurityProvider repositorySecurityProvider = repository.getSecurityProvider() != null
				? repository.getSecurityProvider()
				: null;
		if (repositorySecurityProvider != null) {
			versioningEnabled = repositorySecurityProvider.isVersioningEnabled(fullPath);
			versionCommentsEnabled = repositorySecurityProvider.allowsVersionComments(fullPath);
		}
		String versionComment = null;
		if (!versioningEnabled || !versionCommentsEnabled) {
			versionComment = "";
		} else {
			versionComment = "no comment";
		}

		// 调用kettle 接口 进行数据保存
		repository.save(transMeta, versionComment, null);
		
		this.updateDataBaseGroupNae(transMeta);
		
	}

	/**
	 * 更新组
	 * 
	 * @param transMeta
	 */
	public void updateDataBaseGroupNae(AbstractMeta transMeta) {
		List<DatabaseMeta> databaseMetaList = transMeta.getDatabases();
		for (DatabaseMeta databaseMeta : databaseMetaList) {
		 	DatabaseConnEntity dbConn = new DatabaseConnEntity();
	        dbConn.setDatabaseName(databaseMeta.getDatabaseName());
	        dbConn.setHostName(databaseMeta.getHostname());
	        dbConn.setName(databaseMeta.getName());
	        dbConn.setUserGroup("DataHourglass");
	        cService.updateDatabaseUserName(dbConn);
		}
	}
	
	/**
	 * @see 构建转换xml
	 * @param transName
	 * @param map
	 * @return
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public String initXml(String transName, Map map) throws IOException, ParserConfigurationException, SAXException {
		String parseXmlStr = "";
		try {
			// 文件路径
			String url = "dataHourglassTemplates/InsertUpdate.xml";
			logger.info("开始解析xml文件，路径：{}", url);
			// 1-将文件转换io流
			InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(url);
			Document doc =XmlUtil.readXML(inputStream);
			// 得到 XML 文档的根节点
			Element root = doc.getDocumentElement();
			// 关闭流
			inputStream.close();
			System.out.println("解析完毕");
			logger.info("解析完毕xml文件完成！");

			this.initInfoLlabel(root, transName, map);
			this.initStepLlabel(root, map);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("encoding", "UTF8");// 解决中文问题
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			transformer.transform(new DOMSource(doc), new StreamResult(stream));
			parseXmlStr = stream.toString();

			return parseXmlStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * @see 处理 Info标签
	 * @param root
	 * @param transName
	 * @param map
	 */
	protected void initInfoLlabel(Element root, String transName, Map map ) {
		logger.info("处理Info标签开始！");
		NodeList infoNodeList = root.getElementsByTagName("Info");
		String format = DateUtil.format(new Date(), "yyyy/MM/dd HH:mm:ss.SSS");
		
		Map<String, Object> dataBase = (Map<String, Object>) map.get("databases");
		
		logger.info("数据源信息：{}", dataBase);
		
		for (int i = 0; i < infoNodeList.getLength(); i++) {
			Node node = infoNodeList.item(i);
			if (node instanceof Element) {
				Element element = (Element) node;
				element.setAttribute("name", transName);
				element.setAttribute("created_user", "1");
				element.setAttribute("created_date", format);
				element.setAttribute("modified_date", format);
				
				JSONArray jsonArray = JSONUtil.createArray();
				jsonArray.add(dataBase((Map<String, String>)dataBase.get("from")));
				jsonArray.add(dataBase((Map<String, String>)dataBase.get("to")));
				
				element.setAttribute("databases", jsonArray.toString());
			}
		}
		
		logger.info("处理Info标签结束开始！");
	}
	
	/**
	 * @see 处理 Step标签
	 * @param root
	 * @param map
	 */
	protected void initStepLlabel(Element root, Map map ) {
		logger.info("处理Step标签开始！");
		Map<String, Object> steps = (Map<String, Object>) map.get("steps");
		
		NodeList stepNodeList = root.getElementsByTagName("Step");
		for (int i = 0; i < stepNodeList.getLength(); i++) {
			Node node = stepNodeList.item(i);
			if (node instanceof Element) {
				Element element = (Element) node;
				String ctype = element.getAttribute("ctype");
				Map<String, String> stepMap = (Map<String, String>) steps.get(ctype);
				if ("TableInput".equals(ctype)) {// 表输入
					element.setAttribute("connection", stepMap.get("connection"));
					element.setAttribute("sql", stepMap.get("sql"));
				} else if ("TableOutput".equals(ctype)) {// 表输出
					element.setAttribute("connection", stepMap.get("connection"));
					element.setAttribute("schema", stepMap.get("schema"));
					element.setAttribute("table", stepMap.get("table"));
					element.setAttribute("fields", stepMap.get("fields"));
				} else if ("InsertUpdate".equals(ctype)) {
					element.setAttribute("connection", stepMap.get("connection"));
					element.setAttribute("schema", stepMap.get("schema"));
					element.setAttribute("table", stepMap.get("table"));
					
					element.setAttribute("searchFields", stepMap.get("searchFields"));
					element.setAttribute("updateFields", stepMap.get("updateFields"));
					element.setAttribute("update_bypassed", "N"); //N 执行更新操作   Y:不执行更新操作
				}
			}
		}
	}
	
	/**
	 * mysql 数据库处理
	 * @param dataBase
	 * @return
	 */
	protected JSONObject dataBase(Map<String, String> dataBase) {
		String type = dataBase.get("type");
		logger.info("处理数据源开始,数据源类型：{}", type);
		
		JSONObject dbJson = JSONUtil.parseObj(dataBase);
		JSONArray jsonArray = JSONUtil.createArray();
		dbJson.putOpt("supportBooleanDataType", true);
		dbJson.putOpt("supportTimestampDataType", true);
		dbJson.putOpt("preserveReservedCaseCheck", true);
		dbJson.putOpt("extraOptions", jsonArray);
		dbJson.putOpt("usingConnectionPool", "N");
		dbJson.putOpt("initialPoolSize", 5);
		dbJson.putOpt("maximumPoolSize", 10);
		dbJson.putOpt("partitioned", "N");
		dbJson.putOpt("partitionInfo", jsonArray);
		
		dbJson.putOpt("access", 0);//Native	Native (JDBC) 连接
		
		if("MYSQL".equals(type)) {
			mySqlDataBase(dbJson);
		}
		
		return dbJson;
	}
	
	/**
	 * mysql 数据库配置
	 * @param dbJson
	 */
	protected void mySqlDataBase(JSONObject dbJson) {
		dbJson.putOpt("streamingResults", "on");
	}
	
	/**
	 * @see 手动执行
	 * 
	 * @param path
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/executeTrans")
	protected void executeTrans(@RequestParam String path) throws Exception {
		String graphXml = this.getXml(path);
		runTrans(graphXml);
	}
	
	/**
	 * 执行任务
	 * @param path
	 * @throws Exception
	 */
	protected String getXml(String path) throws Exception {
		String dir = path.substring(0, path.lastIndexOf("/"));
		String name = path.substring(path.lastIndexOf("/") + 1);
		Repository repository = App.getInstance().getRepository();
		RepositoryDirectoryInterface directory = null;
		String graphXml = "";
		try {
			directory = repository.findDirectory(dir);
			if (directory == null)
				directory = repository.getUserHomeDirectory();

			TransMeta transMeta = repository.loadTransformation(name, directory, null, true, null);
			transMeta.setRepositoryDirectory(directory);

			GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
			graphXml = codec.encode(transMeta);

		} catch (Exception e) {
			// 数据库连接出现问题后kettle内部api资源库连接失效需要捕获异常后重新连接
			e.printStackTrace();
			if (e instanceof KettleException) {
				Repository appRepo = App.getInstance().getRepository();
				appRepo.disconnect();
				appRepo.init(App.getInstance().meta);
				appRepo.connect("root", "root123456ABCD!@#");
			}
		}
		
		return graphXml;
	}
}
