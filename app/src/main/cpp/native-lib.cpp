#include <jni.h>
#include <string>
#include <sys/system_properties.h> // For system properties (e.g., kernel version)
#include <unistd.h>                // For sysconf (e.g., CPU cores)
#include <fstream>                 // For reading system files (e.g., RAM)

// Function to get the total RAM size (in bytes)
std::string getRamTotal() {
    long pages = sysconf(_SC_PHYS_PAGES);
    long pageSize = sysconf(_SC_PAGE_SIZE);
    long ramTotal = pages * pageSize;
    return std::to_string(ramTotal);
}

// Function to get CPU information (cores and frequency)
std::string getCpuInfo() {
    long cpuCores = sysconf(_SC_NPROCESSORS_ONLN);
    
    std::string cpuFreq = "0";
    std::ifstream cpuFreqFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
    if (cpuFreqFile.is_open()) {
        cpuFreqFile >> cpuFreq;
        cpuFreqFile.close();
        // Convert kHz to MHz and format with 3 decimal places
        double freqMHz = std::stod(cpuFreq) / 1000.0;
        char freqStr[32];
        snprintf(freqStr, sizeof(freqStr), "%.3f", freqMHz);
        cpuFreq = freqStr;
    }
    
    return "{\"cpu_cores\":\"" + std::to_string(cpuCores) + "\",\"cpu_freq\":\"" + cpuFreq + "\"}";
}

// Function to get the kernel version
std::string getKernelVersion() {
    char kernelVersion[128];
    __system_property_get("ro.build.version.release", kernelVersion);
    return std::string(kernelVersion);
}

// Function to get the build fingerprint
std::string getBuildFingerprint() {
    char buildFingerprint[128];
    __system_property_get("ro.build.fingerprint", buildFingerprint);
    return std::string(buildFingerprint);
}

// Function to get the hardware serial (not available on all devices)
std::string getHardwareSerial() {
    char hardwareSerial[128];
    __system_property_get("ro.serialno", hardwareSerial);
    return std::string(hardwareSerial);
}

// Main JNI function to return native device info as a JSON string
extern "C" JNIEXPORT jstring JNICALL
Java_com_nyinyi_assignment_webviewcontentplayer_SC_1INTERFACE_getNativeInfo(JNIEnv* env, jobject /* this */) {
    try {
        std::string json = "{";
        json += "\"ram_total\":\"" + getRamTotal() + "\",";
        json += "\"cpu_info\":" + getCpuInfo() + ",";
        json += "\"kernel_version\":\"" + getKernelVersion() + "\",";
        json += "\"build_fingerprint\":\"" + getBuildFingerprint() + "\",";
        json += "\"hardware_serial\":\"" + getHardwareSerial() + "\"";
        json += "}";
        
        return env->NewStringUTF(json.c_str());
    } catch (const std::exception& e) {
        std::string errorJson = "{\"error\":\"" + std::string(e.what()) + "\"}";
        return env->NewStringUTF(errorJson.c_str());
    }
}