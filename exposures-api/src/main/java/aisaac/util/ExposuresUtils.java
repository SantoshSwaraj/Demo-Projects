package aisaac.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.Column;

public class ExposuresUtils {


	public static <T> String getTableColumnNameUsingEntityVariable(String name,Class<T> className) {
		for (Field field : className.getDeclaredFields()) {
			Column column = field.getAnnotation(Column.class);
			if (column != null && name.equals(field.getName())) {
				return column.name();
			}
		}
		return null;
	}

	public static <T> List<String> filterGivenColumnNameDataFromList(String columnName, List<?> data,
			Class<T> className) throws NoSuchMethodException, SecurityException {

		Method getterMethod = className.getMethod("get" + StringUtils.capitalize(StringUtils.remove(columnName, '_')));

		List<String> filterdData = data.stream().map(method -> invokeGetterMethod(method, getterMethod))
				.map(Object::toString).collect(Collectors.toList());
		return filterdData;
	}

	private static Object invokeGetterMethod(Object entity, Method getterMethod) {
		try {
			return getterMethod.invoke(entity);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
//	public static <T> List<?> setUserActionSatutsToGivenList(String userAction, List<?> data, String note,
//			Class<T> className) throws NoSuchMethodException, SecurityException {
//
//		Map<String, Object> columnAndStatusMap=getLogFlowDashboardUserActionColumnNameAndValue(userAction,note);
//		
//		for (String key : columnAndStatusMap.keySet()) {
//			Object value = columnAndStatusMap.get(key);
//			Method setterMethod = className.getMethod("set" + StringUtils.capitalize(StringUtils.remove(key.toString(), '_')),value.getClass());
//
//			 data = data.stream().map(method -> invokeSetterMethod(method, setterMethod,value))
//					.collect(Collectors.toList());
//        }
//		
//		
//		return data;
//	}

	private static Object invokeSetterMethod(Object entity, Method setterMethod,Object value) {
		try {
			return setterMethod.invoke(entity, value);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static Long getLocalDateTimeInMilliSec(LocalDateTime date) {
		if(Objects.isNull(date)) {
			return null;
		}
		return date.toInstant(ZoneOffset.UTC).toEpochMilli();
	}
}
