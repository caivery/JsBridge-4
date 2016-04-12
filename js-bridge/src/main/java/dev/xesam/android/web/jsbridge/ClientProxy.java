package dev.xesam.android.web.jsbridge;

import android.os.SystemClock;

import java.util.HashMap;
import java.util.Map;

/**
 * send transaction to server
 * Created by xesamguo@gmail.com on 16-4-7.
 */
class ClientProxy {

    private Map<Long, ClientCallback<?>> callbacks = new HashMap<>();
    private JsBridge mJsBridge;
    private JsExecutor mJsExecutor;

    public ClientProxy(JsBridge mJsBridge) {
        this.mJsBridge = mJsBridge;
        this.mJsExecutor = new JsExecutor(mJsBridge);
    }

    /**
     * [java -> js]
     */
    public void transact(String script) {
        mJsExecutor.transact(script);
    }

    /**
     * [java -> js]
     */
    public void transact(TransactInfo transactInfo, Marshallable invokeParam, ClientCallback<?> clientCallback) {
        if (clientCallback != null) {
            final long callbackId = SystemClock.elapsedRealtime();
            callbacks.put(callbackId, clientCallback);
            transactInfo.setCallbackId(callbackId);
        }
        mJsExecutor.transact(transactInfo, invokeParam);
    }

    /**
     * java -> [js -> java]
     */
    public void dispatchClientCallback(TransactInfo transactInfo, String paramMarshalling) {
        ClientCallback clientCallback = callbacks.get(transactInfo.getInvokeId());
        if (clientCallback != null) {
            clientCallback.onReceiveResult(transactInfo.getInvokeName(), clientCallback.getResult(paramMarshalling));
            callbacks.remove(transactInfo.getInvokeId());
        }
    }
}
