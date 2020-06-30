package com.it.tinkercustom_exercise;
import android.content.Context;
import com.it.tinkercustom_exercise.utils.FileUtils;
import com.it.tinkercustom_exercise.utils.ReflectUtils;
import java.io.File;
import java.lang.reflect.Array;
import java.util.HashSet;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class FixManager {

    private static final FixManager ourInstance = new FixManager();

    public static FixManager getInstance() {
        return ourInstance;
    }

    private FixManager() {
    }

    //存放需要修复的dex集合
    private HashSet<File> loadedDex = new HashSet<>();

    public  void loadFixedDex(Context context) {
        if (context == null)
            return;
        //存放dex文件目录
        File fileDir = context.getDir(FileUtils.dexDirName, Context.MODE_PRIVATE);

        File[] files = fileDir.listFiles();
        for (File file : files) {
            //如果启用了分包的话，classes.dex一般只会存放application类
            if (file.getName().endsWith(".dex") && !"classes.dex".equals(file.getName())) {
                //找到要修复的dex文件
                loadedDex.add(file);
            }
        }
        //创建类加载器
        createDexClassLoader(context, fileDir);
    }

    /**
     * 创建类加载器  DexClassLoader 用于加载外部文件
     *
     * @param context
     * @param fileDir
     */
    private  void createDexClassLoader(Context context, File fileDir) {
        //用来存放加载dex后的临时目录
        String optimizedDirectory = fileDir.getAbsolutePath() + File.separator + "opt_dex";
        File fOpt = new File(optimizedDirectory);
        if (!fOpt.exists()) {
            fOpt.mkdirs();
        }
        DexClassLoader classLoader;
        for (File dex : loadedDex) {
            //初始化类加载器
            classLoader = new DexClassLoader(dex.getAbsolutePath(), optimizedDirectory, null,
                    context.getClassLoader());
            //热修复
            hotFix(classLoader, context);
        }
    }

    private  void hotFix(DexClassLoader myClassLoader, Context context) {
        //系统的类加载器
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        try {
            //重要的来了
            // 获取自己的DexElements数组对象
            Object myDexElements = ReflectUtils.getDexElements(
                    ReflectUtils.getPathList(myClassLoader));
            // 获取系统的DexElements数组对象
            Object sysDexElements = ReflectUtils.getDexElements(
                    ReflectUtils.getPathList(pathClassLoader));
            // 合并
            Object dexElements = combineArray(myDexElements, sysDexElements);
            // 获取系统的 pathList
            Object sysPathList = ReflectUtils.getPathList(pathClassLoader);
            // 重新赋值给系统的 pathList
            ReflectUtils.setField(sysPathList, sysPathList.getClass(), dexElements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 合并数组
     *
     * @param arrayLhs 前数组（插队数组）
     * @param arrayRhs 后数组（已有数组）
     * @return 处理后的新数组
     */
    public  Object combineArray(Object arrayLhs, Object arrayRhs) {
        // 获得一个数组的Class对象，通过Array.newInstance()可以反射生成数组对象
        Class<?> localClass = arrayLhs.getClass().getComponentType();
        // 前数组长度
        int i = Array.getLength(arrayLhs);
        // 新数组总长度 = 前数组长度 + 后数组长度
        int j = i + Array.getLength(arrayRhs);
        // 生成数组对象
        Object result = Array.newInstance(localClass, j);
        for (int k = 0; k < j; ++k) {
            //先把自己的放入数组
            if (k < i) {
                // 从0开始遍历，如果前数组有值，添加到新数组的第一个位置
                Array.set(result, k, Array.get(arrayLhs, k));
            } else {
                // 添加完前数组，再添加后数组，合并完成
                Array.set(result, k, Array.get(arrayRhs, k - i));
            }
        }
        return result;
    }
}
