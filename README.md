## Java层热修复方式学习

### Tinker

**目的：**

​	基于Tinker修复原理，手写class的修复过程。学习这种基于Java层的修复方式原理。

**原理：**

​	利用类加载器做文章。ClassLoader类中loadedClass只会加载一次，如果有bug的类已经被加载了，就算已经下载到了修复的dex包，依然要重启APP后再次加载才能生效。（**基于Java层的修复方式**）

**优劣势：**

​	优势是兼容性比较好；

​	劣势是需要重启app，修复包文件比较大；





### class的修复

​	从服务器下载dex文件，如果修复包存在先删除，然后拷贝到私有目录。
​	

​    1.创建自己的类加载器 (即BaseDexClassLoader的子类DexClassLoader)；

​    2.获取系统的PathClassLoader；

​    3.获取自己的dexElements；

​	4.获取系统的dexElements；

​	5.将系统的dexElements和自己的合并成新的dexElements（先插入修复包的dex）；

​	6.重新赋值给系统的pathList。


 ![img](/Users/lgc/Documents/ASProjects/GithubShareOpen/TinkerCustomExercise/img.png)





### so包修复分析

分析Tinker源码，它的做法比较简单除暴。使用的是通过System.load("so包全路径");方法来加载修复后的so包，只有从而替换了System.loadLibrary("so包名");

因为当我们在程序里调用System.loadLibrary("so包名");最终进到底层 DexPathList类中findLibrary方法中 ，会有判断，如果加载过了，直接返回前面加载过的同名的so文件，从而达到替换的效果。

 

```java
public String findLibrary(String libraryName) {
    //如果之前加载过了绝对路径返回给你
    String fileName = System. mapLibraryName (libraryName) ;
    for (Element element : nativeLibraryPathElements) {
        String path = element.findNativeLibrary (fileName) ;
        if (path != null){
            return path;
        }
    }
    return null ;
}
```

分析时涉及到的源码：

PathClassLoader.java

DexPathList.java

BaseDexClassLoader.java

java_vm_ext.cc

java_lang_Runtime.cc





### resource资源修复分析

本身一个APP只有一个LoadApp， 通过自己创建一个LoadApp（里面包含资源信息），getResouse()最终到底层会去找LoadApp中的资源信息，去装LoadApp的集合中去找，从第一个开始，找到了返回，找不到就再从下个找，直到找不到就报错。

通过将我们的LoadApp插队到系统LoadApp之前，从而达到将修复后的资源加载进去。完成修复。