package com.qwary.internal

import android.util.Log
import android.util.Patterns
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.qwary.QwSettings

private val QW_TAG = "Constants"


fun getInItScript(appId: String): String {
    return """
        var app_id = "$appId";
window.qwSettings = {
  appId: app_id,
  osPlatform: 'Android',
};
!(function () {
  if (!window.qwTracking) {
    (window.qwTracking = Object.assign({}, window.qwTracking, {
      queue:
        window.qwTracking && window.qwTracking.queue
          ? window.qwTracking.queue
          : [],
      track: function (t) {
        console.log("track");
        this.queue.push({ type: "track", props: t });
      },
      init: function (t) {
        console.log("init");     
        this.queue.push({ type: "init", props: t });
      },
    })),
      window.qwSettings;
    var t = function (t) {
      console.log("create",window?.qwSettings?.appId);
      var e = document.createElement("script");
      (e.type = "text/javascript"),
        (e.async = !0),
        (e.src =
          "file:///android_asset/qw.intercept.sdk.merged.js?id=" +
          app_id);
      var n = document.getElementsByTagName("script")[0];
      // Satinder Change as n is undefined  n.parentNode.insertBefore(e,n)
        document.head.appendChild(e);
    };
    "complete" === document.readyState
      ? t()
      : window.attachEvent
        ? window.attachEvent("onload", t)
        : window.addEventListener("load", t, !1);
  }
})(),
  qwTracking.init(qwSettings);
    """.trimIndent()
}

fun getInItScript(appId: String, visitorId: String): String {
    return """
      var app_id = "$appId";
  window.qwSettings = {
      appId: app_id,
      identifier: 'email',
      contact: { //you can also populate custom attributes in this
          firstName: '%CONTACT_FIRST_NAME%',
          lastName: '%CONTACT_LAST_NAME%',
          email: '%CONTACT_EMAIL_NAME%'
      }
  }! function() {
      if (!window.qwTracking) {
          window.qwTracking = Object.assign({}, window.qwTracking, {
              queue: window.qwTracking && window.qwTracking.queue ? window.qwTracking.queue : [],
              track: function (t) {
                  this.queue.push({
                      type: "track",
                      props: t
                  })
              },
              init: function (t) {
                  this.queue.push({
                      type: "init",
                      props: t
                  })
              }
          }), window.qwSettings;
          var t = function (t) {
              var e = document.createElement("script");
              // Satinder Change the e.src
              e.type = "text/javascript", e.async = !0, e.src = "file:///android_asset/qw.intercept.sdk.merged.js?id=" + app_id;
              var n = document.getElementsByTagName("script")[0];
              // Satinder Change as n is undefined  n.parentNode.insertBefore(e,n)
              document.head.appendChild(e);
          };
          "complete" === document.readyState ? t() : window.attachEvent ? window.attachEvent("onload", t) : window.addEventListener("load", t, !1)
      }
  } (), qwTracking.init(qwSettings);
    """.trimIndent()
}

fun getEventTrackScript(eventName: String): String {
    return "qwTracking.track('$eventName');"
}

fun getLogoutScript(): String {
    return "qwTracking.logout();"
}

fun <Model> objectFromString(json: String, classOfModel: Class<Model>): Model? {
    val gson = Gson()
    var model: Model? = null
    try {
        model = gson.fromJson(json, classOfModel)
    } catch (e: JsonSyntaxException) {
        Log.d(QW_TAG, "JsonSyntaxException", e)
    } catch (e: Exception) {
        Log.d(QW_TAG, "Exception", e)
    }
    return model
}

fun <Model> jsonFromModel(model: Model): String? {
    val gson = Gson()
    var json: String? = null
    try {
        json = gson.toJson(model)
    } catch (e: JsonSyntaxException) {
        Log.d(QW_TAG, "JsonSyntaxException", e)
    } catch (e: Exception) {
        Log.d(QW_TAG, "Exception", e)
    }
    Log.d(QW_TAG, "jsonFromModel() called with: model = [$model]$json")
    return json
}

fun QwSettings.toJsonStringify(): String? {
    return jsonFromModel(this)
}
// for passing the object string in the javascript
//fun getInItScript(qwSettings: String): String {
//    val data = objectFromString(qwSettings, QwSettings::class.java)
//    if (Patterns.EMAIL_ADDRESS.matcher(data?.email.toString()).matches()) {
//        return """
//      var app_id = "${data?.appId}";
//  window.qwSettings = {
//      appId: '${data?.appId}',
//      osPlatform: '${data?.osPlatform}',
//      identifier: 'email',
//      contact: { //you can also populate custom attributes in this
//          firstName: '${data?.firstName ?: ""}',
//          lastName: '${data?.lastName ?: ""}',
//          email: '${data?.email ?: ""}'
//      }
//  }! function() {
//      if (!window.qwTracking) {
//          window.qwTracking = Object.assign({}, window.qwTracking, {
//              queue: window.qwTracking && window.qwTracking.queue ? window.qwTracking.queue : [],
//              track: function (t) {
//                  this.queue.push({
//                      type: "track",
//                      props: t
//                  })
//              },
//              init: function (t) {
//                  this.queue.push({
//                      type: "init",
//                      props: t
//                  })
//              }
//          }), window.qwSettings;
//          var t = function (t) {
//              var e = document.createElement("script");
//              // Satinder Change the e.src
//              e.type = "text/javascript", e.async = !0, e.src = "file:///android_asset/qw.intercept.sdk.merged.js?id=" + app_id;
//              var n = document.getElementsByTagName("script")[0];
//              // Satinder Change as n is undefined  n.parentNode.insertBefore(e,n)
//              document.head.appendChild(e);
//          };
//          "complete" === document.readyState ? t() : window.attachEvent ? window.attachEvent("onload", t) : window.addEventListener("load", t, !1)
//      }
//  } (), qwTracking.init(qwSettings);
//    """.trimIndent()
//    } else {
//        return """
//        var app_id = "${data?.appId}";
//window.qwSettings = ${qwSettings};
//!(function () {
//  if (!window.qwTracking) {
//    (window.qwTracking = Object.assign({}, window.qwTracking, {
//      queue:
//        window.qwTracking && window.qwTracking.queue
//          ? window.qwTracking.queue
//          : [],
//      track: function (t) {
//        this.queue.push({ type: "track", props: t });
//      },
//      init: function (t) {
//        this.queue.push({ type: "init", props: t });
//      },
//    })),
//      window.qwSettings;
//    var t = function (t) {
//      console.log("create",window?.qwSettings?.appId);
//      var e = document.createElement("script");
//      (e.type = "text/javascript"),
//        (e.async = !0),
//        (e.src =
//          "file:///android_asset/qw.intercept.sdk.merged.js?id=" +
//          app_id);
//      var n = document.getElementsByTagName("script")[0];
//      // Satinder Change as n is undefined  n.parentNode.insertBefore(e,n)
//        document.head.appendChild(e);
//    };
//    "complete" === document.readyState
//      ? t()
//      : window.attachEvent
//        ? window.attachEvent("onload", t)
//        : window.addEventListener("load", t, !1);
//  }
//})(),
//  qwTracking.init(qwSettings);
//    """.trimIndent()
//    }
//}
