package com.example.administrator.bluetoothdevice.utils;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;


/**
 * Created by 2018/4/12 11:21
 * 创建：Administrator on
 * 描述:蓝牙扫描回调结果
 */
public class Tools {

    public static JSONObject decodeAdvData(byte[] advData) {
        JSONObject jsonAdvData = new JSONObject();
        if (advData != null && advData.length != 0) {
            JSONArray serviceUUIDs = new JSONArray();
            JSONArray solicitedServiceUUIDs = new JSONArray();
            JSONArray overflowServiceUUIDs = new JSONArray();

            byte[] var15;
            for (boolean isOver = true; isOver && advData != null && advData.length != 0; advData = var15) {
                byte dataLen = advData[0];
                if (dataLen == 0) {
                    isOver = false;
                    break;
                }

                byte[] allData = new byte[dataLen];

                for (int type = 0; type < allData.length; ++type) {
                    allData[type] = advData[type + 1];
                }

                byte[] var14 = new byte[]{allData[0]};
                byte[] data = new byte[allData.length - 1];

                int newData;
                for (newData = 0; newData < data.length; ++newData) {
                    data[newData] = allData[newData + 1];
                }

                int i;
                if ((255 & var14[0]) == 2) {
                    var15 = new byte[data.length];

                    for (i = 0; i < var15.length; ++i) {
                        var15[i] = data[data.length - i - 1];
                    }

                    serviceUUIDs.put(bytesToHexString(var15));
                } else {
                    byte[] mByte;
                    if ((255 & var14[0]) == 3) {
                        newData = data.length / 2;

                        for (i = 0; i < newData; ++i) {
                            mByte = new byte[]{data[i * 2], data[i * 2 + 1]};
                            serviceUUIDs.put(bytesToHexString(mByte));
                        }
                    } else if ((255 & var14[0]) == 4) {
                        var15 = new byte[data.length];

                        for (i = 0; i < var15.length; ++i) {
                            var15[i] = data[data.length - i - 1];
                        }

                        serviceUUIDs.put(bytesToHexString(var15));
                    } else if ((255 & var14[0]) == 5) {
                        newData = data.length / 4;

                        for (i = 0; i < newData; ++i) {
                            mByte = new byte[]{data[i * 4], data[i * 4 + 1], data[i * 4 + 2], data[i * 4 + 3]};
                            serviceUUIDs.put(bytesToHexString(mByte));
                        }
                    } else if ((255 & var14[0]) == 6) {
                        var15 = new byte[data.length];

                        for (i = 0; i < var15.length; ++i) {
                            var15[i] = data[data.length - i - 1];
                        }

                        serviceUUIDs.put(bytesToHexString(var15));
                    } else if ((255 & var14[0]) == 7) {
                        newData = data.length / 16;

                        for (i = 0; i < newData; ++i) {
                            mByte = new byte[]{data[i * 16], data[i * 16 + 1], data[i * 16 + 2], data[i * 16 + 3], data[i * 16 + 4], data[i * 16 + 5], data[i * 16 + 6], data[i * 16 + 7], data[i * 16 + 8], data[i * 16 + 9], data[i * 16 + 10], data[i * 16 + 11], data[i * 16 + 12], data[i * 16 + 13], data[i * 16 + 14], data[i * 16 + 15]};
                            serviceUUIDs.put(bytesToHexString(mByte));
                        }
                    } else if ((255 & var14[0]) == 8) {
                        addProperty(jsonAdvData, "localName", hexStrToStr(bytesToHexString(data)));
                    } else if ((255 & var14[0]) == 9) {
                        addProperty(jsonAdvData, "localName", hexStrToStr(bytesToHexString(data)));
                    } else if ((255 & var14[0]) == 10) {
                        addProperty(jsonAdvData, "txPowerLevel", bytesToHexString(data));
                    } else if ((255 & var14[0]) == 18) {
                        addProperty(jsonAdvData, "isConnected", bytesToHexString(data));
                    } else if ((255 & var14[0]) == 20) {
                        newData = data.length / 2;

                        for (i = 0; i < newData; ++i) {
                            mByte = new byte[]{data[i * 2], data[i * 2 + 1]};
                            solicitedServiceUUIDs.put(bytesToHexString(mByte));
                        }
                    } else if ((255 & var14[0]) == 21) {
                        newData = data.length / 16;

                        for (i = 0; i < newData; ++i) {
                            mByte = new byte[]{data[i * 16], data[i * 16 + 1], data[i * 16 + 2], data[i * 16 + 3], data[i * 16 + 4], data[i * 16 + 5], data[i * 16 + 6], data[i * 16 + 7], data[i * 16 + 8], data[i * 16 + 9], data[i * 16 + 10], data[i * 16 + 11], data[i * 16 + 12], data[i * 16 + 13], data[i * 16 + 14], data[i * 16 + 15]};
                            solicitedServiceUUIDs.put(bytesToHexString(mByte));
                        }
                    } else if ((255 & var14[0]) == 22) {
                        addProperty(jsonAdvData, "serviceData", bytesToHexString(data));
                    } else if ((255 & var14[0]) == 255) {
                        addProperty(jsonAdvData, "manufacturerData", encodeBase64(data));
                    }
                }

                var15 = new byte[advData.length - dataLen - 1];

                for (i = 0; i < var15.length; ++i) {
                    var15[i] = advData[i + 1 + dataLen];
                }
            }

            addProperty(jsonAdvData, "serviceUUIDs", serviceUUIDs);
            addProperty(jsonAdvData, "solicitedServiceUUIDs", solicitedServiceUUIDs);
            addProperty(jsonAdvData, "overflowServiceUUIDs", overflowServiceUUIDs);
            addProperty(jsonAdvData, "advData", advData);
            return jsonAdvData;
        } else {
            return jsonAdvData;
        }
    }

    public static void addProperty(JSONObject obj, String key, Object value) {
        try {
            obj.put(key, value);
        } catch (JSONException var4) {
            ;
        }
    }

    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);

        for (int i = 0; i < bArray.length; ++i) {
            String sTemp = Integer.toHexString(255 & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }

            sb.append(sTemp.toUpperCase());
        }

        return sb.toString();
    }

    public static String encodeBase64(byte[] value) {
        return Base64.encodeToString(value, 3);
    }

    public static String hexStrToStr(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];

        for (int i = 0; i < bytes.length; ++i) {
            int n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 255);
        }

        return new String(bytes);
    }

    public static void execCommand(String cmd) {
        Runtime runtime = Runtime.getRuntime();
        DataOutputStream os = null;
        try {
            Process proc = runtime.exec("sh");
            os = new DataOutputStream(proc.getOutputStream());
            os.write(cmd.getBytes());
            os.writeBytes("\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            int ret = proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
