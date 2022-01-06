package tw.hyin.demo.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/*
PojoConvertUtil
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class PojoUtil {

	/*
	 * 變數快取
	 */
	private static final Map<String, Map<String, Field>> cacheFields = new ConcurrentHashMap<>();
	private static final Set<Class> basicClass = new HashSet<>();

	static {
		basicClass.add(Integer.class);
		basicClass.add(Character.class);
		basicClass.add(Byte.class);
		basicClass.add(Float.class);
		basicClass.add(Double.class);
		basicClass.add(Boolean.class);
		basicClass.add(BigInteger.class);
		basicClass.add(Long.class);
		basicClass.add(Short.class);
		basicClass.add(String.class);
		basicClass.add(BigDecimal.class);
	}

	/*
	 * 可忽略空值的複製方法
	 */
	public static void copyProperties(Object src, Object target, Boolean ignoreNull) {
		if (ignoreNull) {
			BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
		} else {
			BeanUtils.copyProperties(src, target);
		}
	}

	private static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<>();
		for (PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null)
				emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}

	/*
	 * 將具有相同屬性的型別進行轉換
	 */
	public static <T> T convertPojo(Object orig, Class<T> targetClass) throws Exception {
		T target = targetClass.newInstance();
		/* 獲取源物件的所有變數 */
		Field[] fields = orig.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (isStatic(field))
				continue;
			/* 獲取目標方法 */
			Field targetField = getTargetField(targetClass, field.getName());
			if (targetField == null)
				continue;
			Object value = getFiledValue(field, orig);
			if (value == null)
				continue;
			Class type1 = field.getType();
			Class type2 = targetField.getType();
			// 兩個型別是否相同
			boolean sameType = type1.equals(type2);
			if (isBasicType(type1)) {
				if (sameType)
					setFieldValue(targetField, target, value);
			} else if (value instanceof Map && Map.class.isAssignableFrom(type2)) {// 對map
				setMap((Map) value, field, targetField, target);
			} else if (value instanceof Set && Set.class.isAssignableFrom(type2)) {// 對set
				setCollection((Collection) value, field, targetField, target);
			} else if (value instanceof List && List.class.isAssignableFrom(type2)) {// 對list
				setCollection((Collection) value, field, targetField, target);
			} else if (value instanceof Enum && Enum.class.isAssignableFrom(type2)) {// 對enum
				setEnum((Enum) value, field, targetField, target);
			} else if (value instanceof java.util.Date &&
					java.util.Date.class.isAssignableFrom(type2))
			{
				// 對日期型別，不處理如joda包之類的擴充套件時間，不處理calendar
				setDate((java.util.Date) value, targetField, type2, target, sameType);
			}
		}
		return target;
	}

	/*
	 * 獲取欄位值
	 */
	private static Object getFiledValue(Field field, Object obj) throws IllegalAccessException {
		// 獲取原有的訪問許可權
		boolean access = field.isAccessible();
		try {
			// 設定可訪問的許可權
			field.setAccessible(true);
			return field.get(obj);
		} finally {
			// 恢復訪問許可權
			field.setAccessible(access);
		}
	}

	/*
	 * 設定方法值
	 */
	private static void setFieldValue(Field field, Object obj, Object value) throws Exception {
		// 獲取原有的訪問許可權
		boolean access = field.isAccessible();
		try {
			// 設定可訪問的許可權
			field.setAccessible(true);
			field.set(obj, value);
		} finally {
			// 恢復訪問許可權
			field.setAccessible(access);
		}
	}

	/*
	 * 轉換list
	 */
	public static <T> List<T> convertPojos(List orig, Class<T> targetClass) throws Exception {
		List<T> list = new ArrayList<>(orig.size());
		for (Object object : orig) {
			list.add(convertPojo(object, targetClass));
		}
		return list;
	}

	/*
	 * 設定Map
	 */
	private static <T> void setMap(Map value, Field origField, Field targetField, T targetObject) throws Exception {
		Type origType = origField.getGenericType();
		Type targetType = targetField.getGenericType();
		if (origType instanceof ParameterizedType && targetType instanceof ParameterizedType) {
			// 泛型型別
			ParameterizedType origParameterizedType = (ParameterizedType) origType;
			Type[] origTypes = origParameterizedType.getActualTypeArguments();
			ParameterizedType targetParameterizedType = (ParameterizedType) targetType;
			Type[] targetTypes = targetParameterizedType.getActualTypeArguments();
			if (origTypes != null && origTypes.length == 2 && targetTypes != null && targetTypes.length == 2) {
				// 正常泛型,檢視第二個泛型是否不為基本型別
				Class clazz = (Class) origTypes[1];
				if (!isBasicType(clazz) && !clazz.equals(targetTypes[1])) {
					// 如果不是基本型別並且泛型不一致，則需要繼續轉換
					Set<Map.Entry> entries = value.entrySet();
					Map targetMap = value.getClass().newInstance();
					for (Map.Entry entry : entries) {
						targetMap.put(entry.getKey(), convertPojo(entry.getValue(), (Class) targetTypes[1]));
					}
					setFieldValue(targetField, targetObject, targetMap);
					return;
				}
			}
		}
		setFieldValue(targetField, targetObject, value);
	}

	/*
	 * 設定集合
	 */
	private static <T> void setCollection(Collection value, Field origField, Field targetField, T targetObject)
			throws Exception
	{
		Type origType = origField.getGenericType();
		Type targetType = targetField.getGenericType();
		if (origType instanceof ParameterizedType && targetType instanceof ParameterizedType) {
			// 泛型型別
			ParameterizedType origParameterizedType = (ParameterizedType) origType;
			Type[] origTypes = origParameterizedType.getActualTypeArguments();
			ParameterizedType targetParameterizedType = (ParameterizedType) targetType;
			Type[] targetTypes = targetParameterizedType.getActualTypeArguments();
			if (origTypes != null && origTypes.length == 1 && targetTypes != null && targetTypes.length == 1) {
				// 正常泛型,檢視第二個泛型是否不為基本型別
				Class clazz = (Class) origTypes[0];
				if (!isBasicType(clazz) && !clazz.equals(targetTypes[0])) {
					// 如果不是基本型別並且泛型不一致，則需要繼續轉換
					Collection collection = value.getClass().newInstance();
					for (Object obj : value) {
						collection.add(convertPojo(obj, (Class) targetTypes[0]));
					}
					setFieldValue(targetField, targetObject, collection);
					return;
				}
			}
		}
		setFieldValue(targetField, targetObject, value);
	}

	/*
	 * 設定列舉型別
	 */
	private static <T> void setEnum(Enum value, Field origField, Field targetField, T targetObject) throws Exception {
		if (origField.equals(targetField)) {
			setFieldValue(targetField, targetObject, value);
		} else {
			// 列舉型別都具有一個static修飾的valueOf方法
			Method method = targetField.getType().getMethod("valueOf", String.class);
			setFieldValue(targetField, targetObject, method.invoke(null, value.toString()));
		}
	}

	/*
	 * 設定日期型別
	 */
	private static <T> void setDate(Date value, Field targetField, Class targetFieldType, T targetObject,
			boolean sameType) throws Exception
	{
		Date date = null;
		if (sameType) {
			date = value;
		} else if (targetFieldType.equals(java.sql.Date.class)) {
			date = new java.sql.Date(value.getTime());
		} else if (targetFieldType.equals(java.util.Date.class)) {
			date = new Date(value.getTime());
		} else if (targetFieldType.equals(java.sql.Timestamp.class)) {
			date = new java.sql.Timestamp(value.getTime());
		}
		setFieldValue(targetField, targetObject, date);
	}

	/*
	 * 獲取適配方法
	 */
	public static Field getTargetField(Class clazz, String fieldName) {
		String classKey = clazz.getName();
		Map<String, Field> fieldMap = cacheFields.get(classKey);
		if (fieldMap == null) {
			fieldMap = new HashMap<>();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (isStatic(field))
					continue;
				fieldMap.put(field.getName(), field);
			}
			cacheFields.put(classKey, fieldMap);
		}
		return fieldMap.get(fieldName);
	}

	/*
	 * 確實是否為基礎型別
	 */
	public static boolean isBasicType(Class clazz) {
		return clazz.isPrimitive() || basicClass.contains(clazz);
	}

	/*
	 * 判斷變數是否有靜態修飾符static
	 */
	public static boolean isStatic(Field field) {
		return (8 & field.getModifiers()) == 8;
	}
}
