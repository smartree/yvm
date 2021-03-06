#ifndef YVM_JAVAOBJECT_H
#define YVM_JAVAOBJECT_H

#include <cstdint>

class JavaClass;

// These types widely use in jvm its internal
struct JType {
    virtual ~JType() = default;
};

#define BASE_OF_JTYPE :public JType

struct JVoid BASE_OF_JTYPE {};

typedef JVoid JRef;

struct JDouble BASE_OF_JTYPE {
    explicit JDouble() = default;
    explicit JDouble(double val) : val(val) {}
    double val = 0.0;
};

struct JFloat BASE_OF_JTYPE {
    explicit JFloat() = default;
    explicit JFloat(float val) : val(val) {}
    float val = 0.0f;
};

struct JInt BASE_OF_JTYPE {
    explicit JInt() = default;
    explicit JInt(int32_t val) : val(val) {}
    int32_t val = 0;
};

struct JLong BASE_OF_JTYPE {
    explicit JLong() = default;
    explicit JLong(int64_t val) : val(val) {}
    int64_t val = 0L;
};

struct JObject BASE_OF_JTYPE {
    explicit JObject() { }

    size_t offset = 0; // Offset on java heap
    const JavaClass* jc{}; // Reference to meta java class
};

struct JArray BASE_OF_JTYPE {
    explicit JArray() { }

    int length = 0; // Length of java array
    size_t offset = 0; // Offset on java heap
};

#define IS_COMPUTATIONAL_TYPE_1(value) (typeid(*value)!=typeid(JDouble) && typeid(*value)!=typeid(JLong))
#define IS_COMPUTATIONAL_TYPE_2(value) (typeid(*value)==typeid(JDouble) || typeid(*value)==typeid(JLong))

#define IS_JINT(x) (typeid(*x)==typeid(JInt))
#define IS_JLong(x) (typeid(*x)==typeid(JLong))
#define IS_JDouble(x) (typeid(*x)==typeid(JDouble))
#define IS_JFloat(x) (typeid(*x)==typeid(JFloat))
#define IS_JObject(x) (typeid(*x)==typeid(JObject))
#define IS_JArray(x) (typeid(*x)==typeid(JArray))

#endif // !YVM_OBJECT_H
