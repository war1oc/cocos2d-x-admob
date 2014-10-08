/*
 * AdmobHelper.cpp
 *
 *  Created on: Oct 8, 2014
 *      Author: war1oc
 */

#include "AdmobHelper.h"

#if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)
#include "platform/android/jni/JniHelper.h"
#include <jni.h>
#include <android/log.h>
#include "HelloWorldScene.h"

const char* NativeActivityClassName = "org/cocos2dx/cpp/AppActivity";

void AdmobHelper::showAd()
{
	cocos2d::JniMethodInfo t;
	if (cocos2d::JniHelper::getStaticMethodInfo(t
			, NativeActivityClassName
			, "showAd"
			, "()V")) {
		t.env->CallStaticVoidMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);
	}
}

void AdmobHelper::hideAd()
{
	cocos2d::JniMethodInfo t;
	if (cocos2d::JniHelper::getStaticMethodInfo(t
			, NativeActivityClassName
			, "hideAd"
			, "()V")) {
		t.env->CallStaticVoidMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);
	}
}

#endif

