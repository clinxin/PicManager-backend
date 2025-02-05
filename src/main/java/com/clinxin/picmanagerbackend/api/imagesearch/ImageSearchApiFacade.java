package com.clinxin.picmanagerbackend.api.imagesearch;

import com.clinxin.picmanagerbackend.api.imagesearch.model.ImageSearchResult;
import com.clinxin.picmanagerbackend.api.imagesearch.sub.GetImageFirstUrlApi;
import com.clinxin.picmanagerbackend.api.imagesearch.sub.GetImageListApi;
import com.clinxin.picmanagerbackend.api.imagesearch.sub.GetImagePageUrlApi;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 搜索图片
 * 补充：门面设计模式
 */
@Slf4j
public class ImageSearchApiFacade {  
  
    /**  
     * 搜索图片  
     *  
     * @param imageUrl  
     * @return  
     */  
    public static List<ImageSearchResult> searchImage(String imageUrl) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        List<ImageSearchResult> imageList = GetImageListApi.getImageList(imageFirstUrl);
        return imageList;  
    }

    /*测试当前接口*/
    public static void main(String[] args) {  
        // 测试以图搜图功能  
        String imageUrl = "https://www.codefather.cn/logo.png";  
        List<ImageSearchResult> resultList = searchImage(imageUrl);  
        System.out.println("结果列表" + resultList);  
    }  
}
