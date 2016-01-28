package hanjie.app.pureweather.utils;

import java.util.ArrayList;

import hanjie.app.pureweather.R;
import hanjie.app.pureweather.bean.WeatherZhiShu;

public class WeatherUtils {


    /**
     * 根据天气的类型名称返回对应的图片资源id类型值
     *
     * @param name 天气类型名称
     * @return 对应的int类型值，0表示位置类型
     */
    public static int getWhiteIconIdByTypeName(String name) {
        int resId = 0;
        if (name.equals("晴")) {
            resId = R.mipmap.ic_sun;
        } else if (name.equals("多云")) {
            resId = R.mipmap.ic_cloudy;
        } else if (name.equals("阴")) {
            resId = R.mipmap.ic_overcast;
        } else if (name.equals("雾")) {
            resId = R.mipmap.ic_fog;
        } else if (name.equals("雨夹雪")) {
            resId = R.mipmap.ic_yujiaxue;
        } else if (name.equals("小雨")) {
            resId = R.mipmap.ic_xiaoyu;
        } else if (name.equals("小雪")) {
            resId = R.mipmap.ic_xiaoxue;
        } else if (name.equals("中雨")) {
            resId = R.mipmap.ic_zhongyu;
        } else if (name.equals("阵雨")) {
            resId = R.mipmap.ic_leizhenyu;
        } else if (name.equals("阵雪")) {
            resId = R.mipmap.ic_zhenxue;
        } else if (name.equals("中雪")) {
            resId = R.mipmap.ic_zhongxue;
        } else if (name.equals("大雪")) {
            resId = R.mipmap.ic_daxue;
        } else if (name.equals("暴雪")) {
            resId = R.mipmap.ic_baoxue;
        } else if (name.equals("大雨")) {
            resId = R.mipmap.ic_dayu;
        } else if (name.equals("暴雨")) {
            resId = R.mipmap.ic_baoyu;
        } else if (name.contains("中雪")) {
            resId = R.mipmap.ic_zhongxue;
        } else if (name.contains("中雨")) {
            resId = R.mipmap.ic_zhongyu;
        } else if (name.contains("大雨")) {
            resId = R.mipmap.ic_dayu;
        } else if (name.contains("暴雨")) {
            resId = R.mipmap.ic_baoyu;
        } else if (name.contains("暴雪")) {
            resId = R.mipmap.ic_baoxue;
        } else if (name.contains("大雪")) {
            resId = R.mipmap.ic_daxue;
        } else if (name.equals("霾")) {
            resId = R.mipmap.ic_mai;
        }
        return resId;
    }

    /**
     * 根据天气的类型名称返回对应的图片资源id类型值
     *
     * @param name 天气类型名称
     * @return 对应的int类型值，0表示位置类型
     */
    public static int getBlackIconIdByTypeName(String name) {
        int resId = 0;
        if (name.equals("晴")) {
            resId = R.mipmap.ic_sun_black;
        } else if (name.equals("多云")) {
            resId = R.mipmap.ic_cloudy_black;
        } else if (name.equals("阴")) {
            resId = R.mipmap.ic_overcast_black;
        } else if (name.equals("雾")) {
            resId = R.mipmap.ic_fog_black;
        } else if (name.equals("雨夹雪")) {
            resId = R.mipmap.ic_yujiaxue_black;
        } else if (name.equals("小雨")) {
            resId = R.mipmap.ic_xiaoyu_black;
        } else if (name.equals("小雪")) {
            resId = R.mipmap.ic_xiaoxue_black;
        } else if (name.equals("中雨")) {
            resId = R.mipmap.ic_zhongyu_black;
        } else if (name.equals("阵雨")) {
            resId = R.mipmap.ic_leizhenyu_black;
        } else if (name.equals("阵雪")) {
            resId = R.mipmap.ic_zhenxue_black;
        } else if (name.equals("中雪")) {
            resId = R.mipmap.ic_zhongxue_black;
        } else if (name.equals("大雪")) {
            resId = R.mipmap.ic_daxue_black;
        } else if (name.equals("暴雪")) {
            resId = R.mipmap.ic_baoxue_black;
        } else if (name.equals("大雨")) {
            resId = R.mipmap.ic_dayu_black;
        } else if (name.equals("暴雨")) {
            resId = R.mipmap.ic_baoyu_black;
        } else if (name.contains("中雪")) {
            resId = R.mipmap.ic_zhongxue_black;
        } else if (name.contains("中雨")) {
            resId = R.mipmap.ic_zhongyu_black;
        } else if (name.contains("大雨")) {
            resId = R.mipmap.ic_dayu_black;
        } else if (name.contains("暴雨")) {
            resId = R.mipmap.ic_baoyu_black;
        } else if (name.contains("暴雪")) {
            resId = R.mipmap.ic_baoxue_black;
        } else if (name.contains("大雪")) {
            resId = R.mipmap.ic_daxue_black;
        } else if (name.equals("霾")) {
            resId = R.mipmap.ic_mai_black;
        }
        return resId;
    }


    /**
     * 根据指数集合获取指数所对应的icon资源id集合
     *
     * @param zhiShuList 指数集合
     * @return 对应的icon资源id集合
     */
    public static ArrayList<Integer> getWhiteLivingIndexIconList(ArrayList<WeatherZhiShu> zhiShuList) {
        ArrayList<Integer> resIdList = new ArrayList<Integer>();
        for (int i = 0; i < zhiShuList.size(); i++) {
            if (zhiShuList.get(i).getName().equals("晾晒指数")) {
                resIdList.add(R.mipmap.ic_liangshaizhishu_white);
            } else if (zhiShuList.get(i).getName().equals("洗车指数")) {
                resIdList.add(R.mipmap.ic_xichezhishu_white);
            } else if (zhiShuList.get(i).getName().equals("穿衣指数")) {
                resIdList.add(R.mipmap.ic_chuanyizhishu_white);
            } else if (zhiShuList.get(i).getName().equals("防晒指数")) {
                resIdList.add(R.mipmap.ic_ziwaixian_white);
            } else if (zhiShuList.get(i).getName().equals("运动指数")) {
                resIdList.add(R.mipmap.ic_yundongzhishu_white);
            }
        }
        return resIdList;
    }

    /**
     * 根据温度值的集合，获取对应的icon的集合
     *
     * @param areaRTTempList 温度值的集合
     * @return 对应的icon的集合
     */
    public static ArrayList<Integer> getTempIconIdList(ArrayList<String> areaRTTempList) {
        ArrayList<Integer> tempIconIdList = new ArrayList<Integer>();
        for (int i = 0; i < areaRTTempList.size(); i++) {
            String temp = areaRTTempList.get(i);
            if (!temp.equals("N/A")) {
                int tempInt = Integer.valueOf(temp);
                if (tempInt < 0) {
                    tempIconIdList.add(i, R.mipmap.ic_circle_1);
                } else if (tempInt >= 0 && tempInt < 10) {
                    tempIconIdList.add(i, R.mipmap.ic_circle_2);
                } else if (tempInt >= 10 && tempInt < 20) {
                    tempIconIdList.add(i, R.mipmap.ic_circle_3);
                } else if (tempInt >= 20 && tempInt < 30) {
                    tempIconIdList.add(i, R.mipmap.ic_circle_4);
                } else {
                    tempIconIdList.add(i, R.mipmap.ic_circle_5);
                }
            } else {
                tempIconIdList.add(i, R.mipmap.ic_circle_1);
            }
        }
        return tempIconIdList;
    }
}
