package com.ubirouting.bytelib;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Change an object to byte array, or change an byte array to Object
 *
 * @author YangTao & WangYan
 * @version 1.0
 */
public final class ByteUtils {

    private ByteUtils(){
        throw new UnsupportedOperationException("can't create this object");
    }


    /**
     * convert a object which has {@link ToByte} fields into byte[] buffer.
     *
     * @param obj object to convert
     * @return byte arrays
     */
    public static byte[] toByte(Object obj) {
        List<byte[]> listBytes = new ByteLinkedList();
        encodeObj(obj, listBytes);
        int length = ((ByteLinkedList) listBytes).getByteLength();
        byte[] finalBytes = new byte[length];
        int position = 0;
        for (byte[] bs : listBytes) {
            System.arraycopy(bs, 0, finalBytes, position, bs.length);
            position += bs.length;
        }
        return finalBytes;
    }

    /**
     * to encode object
     *
     * @param comm
     * @param listBytes
     */
    private static void encodeObj(Object comm, List<byte[]> listBytes) {
        Class c = comm.getClass();

        List<Field> fieldsArray = allFields(c);

        List<Field> fieldList = new ArrayList<>();
        for (Field field : fieldsArray) {
            ToByte annotation = field.getAnnotation(ToByte.class);
            if (annotation != null && annotation.order() != -1) {
                fieldList.add(field);
            }
        }

        Collections.sort(fieldList, (o1, o2) -> {
            ToByte annotation = o1.getAnnotation(ToByte.class);
            ToByte annotation2 = o2.getAnnotation(ToByte.class);

            return annotation.order() - annotation2.order();
        });

        for (Field field : fieldList) {
            field.setAccessible(true);
            String typeStr = field.getGenericType().toString();


            if (field.getType().isArray()) {
                // if is an array
                encodeArray(typeStr, field, comm, listBytes);


            } else if (Collection.class.isAssignableFrom(field.getType())) {
                // if is an collection
                encodeList(typeStr, field, comm, listBytes);

            } else {
                encodeField(typeStr, field, comm, listBytes);
            }
        }
    }

    private static void encodeList(String typeStr, Field field, Object comm, List<byte[]> listBytes) {
        try {
            if (typeStr.contains("<java.lang.String>")) {
                Collection<String> list;
                list = (Collection<String>) field.get(comm);

                int length = list.size();
                listBytes.add(PrimaryDatas.i2b(length));
                for (String object : list) {
                    listBytes.add(encodeString(object));
                }


                // handle Long
            } else if (typeStr.contains("<java.lang.Long>")) {

                Collection<Long> list = (Collection<Long>) field.get(comm);
                int length = list.size();
                listBytes.add(PrimaryDatas.i2b(length));

                byte[] longBytes = new byte[length * Long.BYTES];

                int index = 0;
                for (Long num : list) {
                    PrimaryDatas.l2b(num, longBytes, (index++) << 3);
                }

                listBytes.add(longBytes);

                // handle Integer
            } else if (typeStr.contains("<java.lang.Integer>")) {
                Collection<Integer> list = (Collection<Integer>) field.get(comm);
                int length = list.size();
                listBytes.add(PrimaryDatas.i2b(length));

                byte[] intBytes = new byte[length * Integer.BYTES];

                int index = 0;
                for (Integer num : list) {
                    PrimaryDatas.i2b(num, intBytes, (index++) << 2);
                }

                listBytes.add(intBytes);

                // handle Short
            } else if (typeStr.contains("<java.lang.Short>")) {
                Collection<Short> list = (Collection<Short>) field.get(comm);
                int length = list.size();
                listBytes.add(PrimaryDatas.i2b(length));

                byte[] shortBytes = new byte[length * Short.BYTES];

                int index = 0;
                for (Short num : list) {
                    PrimaryDatas.s2b(num, shortBytes, (index++) << 1);
                }

                listBytes.add(shortBytes);

                // handle Byte
            } else if (typeStr.contains("<java.lang.Byte>") || typeStr.contains("<java.lang.Char>")) {
                Collection<Byte> list = (Collection<Byte>) field.get(comm);
                int length = list.size();
                listBytes.add(PrimaryDatas.i2b(length));

                byte[] byteBytes = new byte[length];

                int index = 0;
                for (Object num : list) {
                    byte numB = (Byte) num;
                    byteBytes[index++] = numB;
                }

                listBytes.add(byteBytes);

                // handle Double
            } else if (typeStr.contains("<java.lang.Double>")) {
                Collection<Double> list = (Collection<Double>) field.get(comm);
                int length = list.size();
                listBytes.add(PrimaryDatas.i2b(length));

                byte[] doubleBytes = new byte[length * Double.BYTES];

                int index = 0;
                for (Double num : list) {
                    PrimaryDatas.d2b(num, doubleBytes, (index++) << 3);
                }

                listBytes.add(doubleBytes);

                // handle Object
            } else {
                Collection<Object> list = (Collection<Object>) field.get(comm);
                int length = list.size();
                listBytes.add(PrimaryDatas.i2b(length));
                for (Object object : list) {
                    encodeObj(object, listBytes);
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private static void encodeArray(String typeStr, Field field, Object comm, List<byte[]> listBytes) {
        try {
            Object[] objs = (Object[]) field.get(comm);
            int length = objs.length;
            listBytes.add(PrimaryDatas.i2b(length));

            if (field.getType().equals(String[].class)) {
                for (String string : (String[]) objs) {
                    listBytes.add(encodeString(string));
                }
            } else if (field.getType().equals(byte[].class) || field.getType().equals(Byte[].class)) {
                ByteBuffer bf = ByteBuffer.allocate(objs.length);
                for (Byte b : (Byte[]) objs) {
                    bf.put(b);
                }
                listBytes.add(bf.array());
            } else if (field.getType().equals(int[].class) || field.getType().equals(Integer[].class)) {
                ByteBuffer bf = ByteBuffer.allocate(objs.length * 4);
                for (Integer inte : (Integer[]) objs) {
                    bf.put(PrimaryDatas.i2b(inte));
                }
                listBytes.add(bf.array());
            } else if (field.getType().equals(float[].class) || field.getType().equals(Float[].class)) {
                ByteBuffer bf = ByteBuffer.allocate(objs.length * 4);
                for (Float flo : (Float[]) objs) {
                    bf.put(PrimaryDatas.f2b(flo));
                }
                listBytes.add(bf.array());
            } else if (field.getType().equals(double[].class) || field.getType().equals(Double[].class)) {
                ByteBuffer bf = ByteBuffer.allocate(objs.length * 8);
                for (Double dbl : (Double[]) objs) {
                    bf.put(PrimaryDatas.d2b(dbl));
                }
                listBytes.add(bf.array());
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void encodeField(String typeStr, Field field, Object comm, List<byte[]> listBytes) {
        try {

            Class<?> fieldType = field.getType();
            if (fieldType.equals(String.class)) {
                String str = (String) field.get(comm);
                if (str == null) {
                    // Log.e("ByteUtils", "str is null:" + field.getName());
                    // System.out.println();
                }
                listBytes.add(encodeString(str));

            } else if (fieldType.equals(byte.class) || fieldType.equals(Byte.class)) {
                // 如果使用封装类 则 getByte会报错
                // Byte b = field.getByte(comm);
                // 封装类和 关键字都可以使用(效率可能稍慢(也许))

                byte[] byteBytes = new byte[1];
                byteBytes[0] = (Byte) field.get(comm);
                listBytes.add(byteBytes);

            } else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
                listBytes.add(PrimaryDatas.l2b((Long) field.get(comm)));

            } else if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
                listBytes.add(PrimaryDatas.i2b((Integer) field.get(comm)));

            } else if (fieldType.equals(float.class) || fieldType.equals(Float.class)) {
                listBytes.add(PrimaryDatas.f2b((Float) field.get(comm)));

            } else if (fieldType.equals(short.class) || fieldType.equals(Short.class)) {
                listBytes.add(PrimaryDatas.s2b((Short) field.get(comm)));


            } else {
                Object obj = field.get(comm);
                encodeObj(obj, listBytes);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static byte[] encodeString(String str) {
        int length = str.getBytes().length;

        byte[] stringBytes = new byte[length + 4];
        System.arraycopy(PrimaryDatas.i2b(length), 0, stringBytes, 0, 4);
        System.arraycopy(str.getBytes(), 0, stringBytes, 4, length);

        return stringBytes;
    }

    /**
     * convert a byte array into a object
     *
     * @param bs    byte array
     * @param clazz class of the object
     * @return object
     */
    // public static Object toObject(byte[] bs, Class<?> clazz) {
    // ParseInteger pInt = new ParseInteger();
    // pInt.index = 0;
    // Object comm = decodeBytes(bs, clazz, pInt);
    // return comm;
    // }
    public static <T> T toObject(byte[] bs, Class<T> clazz) {

        ParseInteger pInt = new ParseInteger();
        pInt.index = 0;
        Object comm = decodeBytes(bs, clazz, pInt);
        return (T) comm;
    }

    public static <T extends Object> T toObject(byte[] bs, Class<T> clazz, int start) {

        ParseInteger pInt = new ParseInteger();
        pInt.index = start;
        Object comm = decodeBytes(bs, clazz, pInt);
        return (T) comm;
    }

    private static Object decodeBytes(byte[] bs, Class<?> clazz, ParseInteger pInt) {
        Object comm = null;
        try {
            comm = clazz.newInstance();

            List<Field> fieldsGrop = allFields(clazz);

            List<Field> fieldList = new ArrayList<Field>();
            for (Field field : fieldsGrop) {
                ToByte annotation = field.getAnnotation(ToByte.class);
                if (annotation != null && annotation.order() != -1) {
                    fieldList.add(field);
                }
            }

            Collections.sort(fieldList, new Comparator<Field>() {
                @Override
                public int compare(Field o1, Field o2) {
                    ToByte annotation = o1.getAnnotation(ToByte.class);
                    ToByte annotation2 = o2.getAnnotation(ToByte.class);

                    return annotation.order() - annotation2.order();
                }

            });

            for (Field field : fieldList) {
                field.setAccessible(true);
                String typeStr = field.getGenericType().toString();

                if (field.getType().isArray()) {
                    decodeArray(typeStr, pInt, bs, field, comm);

                } else if (Collection.class.isAssignableFrom(field.getType())) {
                    decodeList(typeStr, pInt, bs, field, comm);

                } else {
                    decodeField(typeStr, pInt, bs, field, comm);
                }
            }

        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return comm;
    }

    private static void decodeList(String typeStr, ParseInteger pInt, byte[] bs, Field field, Object comm) {
        try {
            int forLength = PrimaryDatas.b2i(bs, pInt.index);
            pInt.index += 4;

            Collection collection = createCollection(field);
            if (typeStr.contains("<java.lang.String>")) {
                for (int i = 0; i < forLength; i++) {
                    int length = PrimaryDatas.b2i(bs, pInt.index);
                    pInt.index += 4;
                    String str = PrimaryDatas.b2String(bs, pInt.index, pInt.index + length);
                    pInt.index += length;
                    collection.add(str);
                }
                field.set(comm, collection);

            } else if (typeStr.contains("<java.lang.Long>")) {
                for (int i = 0; i < forLength; i++) {
                    collection.add(PrimaryDatas.b2l(bs, pInt.index));
                    pInt.index += 8;
                }
                field.set(comm, collection);

            } else if (typeStr.contains("<java.lang.Integer>")) {
                for (int i = 0; i < forLength; i++) {
                    collection.add(PrimaryDatas.b2i(bs, pInt.index));
                    pInt.index += 4;
                }
                field.set(comm, collection);

            } else if (typeStr.contains("<java.lang.Short>")) {
                for (int i = 0; i < forLength; i++) {
                    collection.add(PrimaryDatas.b2s(bs, pInt.index));
                    pInt.index += 2;
                }
                field.set(comm, collection);

            } else if (typeStr.contains("<java.lang.Byte>")) {
                for (int i = 0; i < forLength; i++) {
                    collection.add(bs[pInt.index]);
                    pInt.index += 1;
                }
                field.set(comm, collection);

            } else if (typeStr.contains("<java.lang.Float>")) {
                for (int i = 0; i < forLength; i++) {
                    collection.add(PrimaryDatas.b2f(bs, pInt.index));
                    pInt.index += 4;
                }
                field.set(comm, collection);

            } else {

                String listTypeStr = typeStr.substring(typeStr.indexOf("<") + 1, typeStr.length() - 1);
                for (int i = 0; i < forLength; i++) {
                    collection.add(decodeBytes(bs, Class.forName(listTypeStr), pInt));
                }
                field.set(comm, collection);
            }

        } catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Collection createCollection(Field field) {
        if (field.getType().equals(ArrayList.class) || field.getType().equals(List.class))
            return new ArrayList<>();
        else if (field.getType().equals(LinkedList.class) || field.getType().equals(Queue.class))
            return new LinkedList<>();
        else if (field.getType().equals(Set.class) || field.getType().equals(HashMap.class))
            return new HashSet<>();

        throw new IllegalArgumentException("unknown type of field " + field.getName() + "(" + field.getType() + ")");
    }

    private static void decodeField(String typeStr, ParseInteger pInt, byte[] bs, Field field, Object comm)
            throws IllegalArgumentException, IllegalAccessException {

        Object obj = null;
        Class<?> typeField = field.getType();
        if (typeField.equals(String.class)) {
            int length = PrimaryDatas.b2i(bs, pInt.index);
            pInt.index += 4;
            String str = PrimaryDatas.b2String(bs, pInt.index, pInt.index + length);
            obj = str;
            pInt.index += length;
        } else if (typeField.equals(Byte.class) || typeField.equals(byte.class)) {
            obj = bs[pInt.index];
            pInt.index += 1;
        } else if (typeField.equals(Long.class) || typeField.equals(long.class)) {
            obj = PrimaryDatas.b2l(bs, pInt.index);
            pInt.index += 8;
        } else if (typeField.equals(Integer.class) || typeField.equals(int.class)) {
            obj = PrimaryDatas.b2i(bs, pInt.index);
            pInt.index += 4;
        } else if (typeField.equals(Float.class) || typeField.equals(float.class)) {
            obj = PrimaryDatas.b2f(bs, pInt.index);
            pInt.index += 4;
        } else if (typeField.equals(Short.class) || typeField.equals(short.class)) {
            obj = PrimaryDatas.b2s(bs, pInt.index);
            pInt.index += 2;
        } else {
//            System.out.println("encodeField: " + typeStr);
            try {
                String key = "class ";
                String fieldTypeString = typeStr.substring(typeStr.indexOf(key) + key.length());
                obj = decodeBytes(bs, Class.forName(fieldTypeString), pInt);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        field.set(comm, obj);
    }

    private static void decodeArray(String typeStr, ParseInteger pInt, byte[] bs, Field field, Object comm)
            throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {

        int forLength = PrimaryDatas.b2i(bs, pInt.index);
        pInt.index += 4;
        Object[] objs = null;

        Class<?> fieldType = field.getType();

        if (fieldType.equals(String.class)) {
            objs = new String[forLength];
            for (int i = 0; i < forLength; i++) {
                int length = PrimaryDatas.b2i(bs, pInt.index);
                pInt.index += 4;
                String str = PrimaryDatas.b2String(bs, pInt.index, pInt.index + length);
                pInt.index += length;
                objs[i] = str;
            }
        } else if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
            objs = new Byte[forLength];
            for (int i = 0; i < forLength; i++) {
                objs[i] = bs[pInt.index + i];
            }
            pInt.index += forLength;
        } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
            objs = new Integer[forLength];
            for (int i = 0; i < forLength; i++) {
                objs[i] = PrimaryDatas.b2i(bs, pInt.index);
                pInt.index += 4;
            }
        } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
            objs = new Float[forLength];
            for (int i = 0; i < forLength; i++) {
                objs[i] = PrimaryDatas.b2f(bs, pInt.index);
                pInt.index += 4;
            }
        } else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
            objs = new Short[forLength];
            for (int i = 0; i < forLength; i++) {
                objs[i] = PrimaryDatas.b2s(bs, pInt.index);
                pInt.index += 2;
            }
        } else {
            objs = new Object[forLength];
            String key = "class ";
            String fieldTypeString = typeStr.substring(typeStr.indexOf(key) + key.length());
            for (int i = 0; i < forLength; i++) {
                objs[i] = decodeBytes(bs, Class.forName(fieldTypeString), pInt);
                pInt.index += 2;
            }
        }

        field.set(comm, objs);
    }

    private static class ParseInteger {
        int index;
    }

    public static List<Field> allFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<Field>();

        Class<?> nowClass = clazz;
        do {
            fieldList.addAll(Arrays.asList(nowClass.getDeclaredFields()));
        } while ((nowClass = nowClass.getSuperclass()) != null);

        return fieldList;
    }

    private static class ByteLinkedList extends LinkedList<byte[]> {

        private int mByteLength = 0;

        @Override
        public boolean add(byte[] bytes) {
            if (bytes != null)
                mByteLength += bytes.length;
            return super.add(bytes);
        }

        public int getByteLength() {
            return mByteLength;
        }
    }

}
