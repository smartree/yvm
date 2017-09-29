package rtstruct;

import rtstruct.meta.MetaClass;
import rtstruct.rtexception.VMExecutionException;
import ycloader.YClassLoader;
import ycloader.exception.ClassInitializingException;
import ycloader.exception.ClassLinkingException;
import ycloader.exception.ClassLoadingException;
import yvm.adt.Tuple4;
import yvm.adt.Tuple6;
import yvm.auxil.Peel;

import java.util.Map;

public class YObject{
    private MetaClass metaClassReference;
    private Object[] fields;

    /***************************************************************
     *  create a primitive type YObject. as*() methods stuffing this
     *  object as if assignment operation.
     *
     *  And all deriveFrom(_Primitive x) methods are static methods,
     *  they can create a YObject object representing primitive type
     *
     ***************************************************************/
    public YObject(){
        fields = new Object[1];
        metaClassReference = null;
    }

    /***************************************************************
     *  create a array type YObject. Note that the YObject object of
     *  the array type just store components , the length of the
     *  array is stored at YArray object.
     *
     ***************************************************************/

    public YObject(int dimension) {
        fields = new Object[dimension];
        metaClassReference = null;
    }

    /***************************************************************
     *  create a generic object
     *
     ***************************************************************/
    public YObject(YClassLoader loader, MetaClass metaClass) {
        /***************************************************************
         *  get all fields of this class
         *
         ***************************************************************/
        Map fieldDesc = metaClass.fields.getFields();
        int fieldsNum = fieldDesc.size();

        /***************************************************************
         *  create real fields, they will be used to store fields data
         *
         ***************************************************************/
        fields = new Object[fieldsNum];

        /***************************************************************
         *  associate this object with meta class information
         *
         ***************************************************************/
        metaClassReference = metaClass;

        /***************************************************************
         *  create a constant class,because in anonymous function, any
         *  reference which referred to outer state required to FINAL
         *
         ***************************************************************/

        class Counter {
            private int i = 0;

            public void inc() {
                i++;
            }

            public int get() {
                return i;
            }

        }
        final Counter counter = new Counter();
        /***************************************************************
         *  traverse all fields and initiate them using default value
         *  according to JVM (R) 8 SPEC
         *
         ***************************************************************/
        fieldDesc.forEach((_Unused, bundle) -> {
            counter.inc();
            //if this field is not loaded into vm, load it right now
            if (!loader
                    .getStartupThread()
                    .runtimeVM()
                    .methodScope()
                    .existClass(Peel.peelDescriptor(((Tuple4) bundle).get2Placeholder().toString()), loader.getClass())) {
                try {
                    Tuple6 bundle1 = loader.loadClass(Peel.peelDescriptor(((Tuple4) bundle).get2Placeholder().toString()));
                    MetaClass meta = loader.linkClass(bundle1);
                    loader.getStartupThread().runtimeVM().methodScope().addMetaClass(meta);
                    loader.loadInheritanceChain(meta.superClassName);
                    loader.initializeClass(meta);
                } catch (ClassInitializingException | ClassLinkingException | ClassLoadingException e) {
                    throw new VMExecutionException("can not load class" + Peel.peelDescriptor(Peel.peelDescriptor(((Tuple4) bundle).get2Placeholder().toString()))
                            + " while executing anewarray opcode");
                }
            }

            //if is a primitive type, assign a default value, or create a new instance
            switch (Peel.peelDescriptor(((Tuple4) bundle).get2Placeholder().toString())) {
                case "java/lang/Byte":
                case "java/lang/Short":
                case "java/lang/Long":
                case "java/lang/Integer":
                    fields[counter.get()] = 0;
                    break;
                case "java/lang/Character":
                    fields[counter.get()] = '\u0000';
                    break;
                case "java/lang/Double":
                case "java/lang/Float":
                    fields[counter.get()] = 0.0;
                    break;
                case "java/lang/Boolean":
                    fields[counter.get()] = false;
                    break;
                default:
                    fields[counter.get()] = new YObject(loader,
                            loader.getStartupThread()
                                    .runtimeVM()
                                    .methodScope()
                                    .getMetaClass(
                                            Peel.peelDescriptor(
                                                    ((Tuple4) bundle).get2Placeholder().toString()),
                                            loader.getClass()));
                    break;
            }

        });
    }

    @SuppressWarnings("unused")
    public static YObject derivedFrom(int x) {
        return new YObject().asInteger(x);
    }

    @SuppressWarnings("unused")
    public static YObject derivedFrom(long x) {
        return new YObject().asLong(x);
    }

    @SuppressWarnings("unused")
    public static YObject derivedFrom(double x) {
        return new YObject().asDouble(x);
    }

    @SuppressWarnings("unused")
    public static YObject derivedFrom(float x) {
        return new YObject().asFloat(x);
    }

    @SuppressWarnings("unused")
    public static YObject derivedFrom(String x) {
        return new YObject().asString(x);
    }

    @SuppressWarnings("unused")
    YObject asInteger(int x){
        fields[0] = x;
        return this;
    }

    @SuppressWarnings("unused")
    YObject asLong(long x){
        fields[0] = x;
        return this;
    }

    @SuppressWarnings("unused")
    YObject asDouble(double x){
        fields[0] = x;
        return this;
    }

    @SuppressWarnings("unused")
    YObject asFloat(float x){
        fields[0] = x;
        return this;
    }

    @SuppressWarnings("unused")
    YObject asString(String x) {
        fields[0] = x;
        return this;
    }

    @SuppressWarnings("unused")
    public int toInteger(){
        return (int) fields[0];
    }

    @SuppressWarnings("unused")
    public long toLong(){
        return (long) fields[0];
    }

    @SuppressWarnings("unused")
    public double toDouble(){
        return (double) fields[0];
    }

    @SuppressWarnings("unused")
    public float toFloat(){
        return (float) fields[0];
    }

    @SuppressWarnings("unused")
    public short toShort() {
        return (short) fields[0];
    }

    public void setArrayComponent(int index, YObject value) {
        fields[index] = value;
    }

    public YObject getArrayComponent(int index) {
        return (YObject) fields[index];
    }

    public String getClassName() {
        return metaClassReference.qualifiedClassName;
    }
}
