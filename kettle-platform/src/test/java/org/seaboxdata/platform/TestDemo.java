package org.seaboxdata.platform;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.seaboxdata.ext.utils.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestDemo {
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		String json = "{fields:[{id:ID_BATCH,enabled:true,name:ID_BATCH,description:批次 ID},{id:CHANNEL_ID,enabled:true,name:CHANNEL_ID,description:Logging channel ID},{id:LOG_DATE,enabled:true,name:LOG_DATE,description:Log date},{id:LOGGING_OBJECT_TYPE,enabled:true,name:LOGGING_OBJECT_TYPE,description:Object type},{id:OBJECT_NAME,enabled:true,name:OBJECT_NAME,description:Name of the object},{id:OBJECT_COPY,enabled:true,name:OBJECT_COPY,description:Object (usually step) copy},{id:REPOSITORY_DIRECTORY,enabled:true,name:REPOSITORY_DIRECTORY,description:Repository directory of the object},{id:FILENAME,enabled:true,name:FILENAME,description:Filename},{id:OBJECT_ID,enabled:true,name:OBJECT_ID,description:Repository Object ID},{id:OBJECT_REVISION,enabled:true,name:OBJECT_REVISION,description:Repository object revision},{id:PARENT_CHANNEL_ID,enabled:true,name:PARENT_CHANNEL_ID,description:Logging channel ID of the parent object},{id:ROOT_CHANNEL_ID,enabled:true,name:ROOT_CHANNEL_ID,description:Channel ID of the object that logged this information.}]}";
		JSONObject jsonObject = new JSONObject();

		ObjectMapper mapper = new ObjectMapper();
		mapper.readValue(json, JSONObject.class);
	}
}
