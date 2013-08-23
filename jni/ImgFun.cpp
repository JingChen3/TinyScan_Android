#include <jni.h>
#include "cv.h"
#include <highgui.h>
#include "com_appxy_tools_LibImgFun.h"
#include <string.h>
#include <opencv2/imgproc/imgproc_c.h>
#include <android/log.h>

using namespace cv;
#define PI 3.141592
#define HOUGH_LINE_MISTAKE_DIST 20



CvPoint pt[4];


CvPoint getCrossPointFromTwoLine(CvPoint pt1, CvPoint pt2, CvPoint pt3 ,CvPoint pt4)
{

    //第一条直线
    double x1 =  pt1.x, y1 = pt1.y, x2 =  pt2.x, y2 = pt2.y;
    //double a = (y1 - y2) / (x1 - x2);
    //double b = (x1 * y2 - x2 * y1) / (x1 - x2);

    //第二条
    double x3 =  pt3.x, y3 =  pt3.y, x4 =  pt4.x, y4 =  pt4.y;
    //double c = (y3 - y4) / (x3 - x4);
    //double d = (x3 * y4 - x4 * y3) / (x3 - x4);

    double x = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1))
    / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));

    double y = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1) * (y3 - y4))
    / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));

    CvPoint p ;
    p.x = x;
    p.y = y;
    return p;
}

void getedgeDetectByImg_Line(IplImage* _img)
{
    if(_img == NULL)
    	return;
   // p1->x =0;
   // p1->y =0;


    //p2->x =0 ;
    //p2->y =0;

   // p3->x =0 ;
   // p3->y =0 ;

   // p4->x =0;
   // p4->y =0 ;
    float w =  _img->width;
    float h =  _img->height;
    IplImage* src = _img;
    IplImage* srcimg = cvCreateImage(cvGetSize( src), 8, 1 );
    cvSetImageROI( src, cvRect( 0, 0, src->width, src->height ));

    cvSetImageCOI( src,  1);
    cvCopy( src, srcimg, NULL );
    CvMemStorage* storage = cvCreateMemStorage(0);
    CvSeq* lines = 0;
   // int i;

    cvSetImageROI( src, cvRect( 0, 0, src->width, src->height ));

    cvSetImageCOI( src,  1);
    cvCopy( src, srcimg, NULL );

    IplImage* color_dst = cvCreateImage( cvGetSize(src), 8, 4 );
    // color_dst = cvCreateImage( cvGetSize(src), 8, 4 );

    cvCvtColor( srcimg, color_dst, CV_GRAY2BGR );

    lines = cvHoughLines2( srcimg, storage, CV_HOUGH_STANDARD, 1, CV_PI/180,20, 0, 0);
    __android_log_print(ANDROID_LOG_VERBOSE, "sad",  "originalImage has value %d",lines->total);

    float  line_HT[2];
    float line_HB[2];
    float line_VL[2];
    float line_VR[2];

    int hasHT,hasHB,hasVL,hasVR;
    hasHT = 0;
    hasHB = 0;
    hasVL = 0;
    hasVR = 0;



    for(int i = 0; i < MIN(lines->total,100) ; i++ )
    {

        if(  hasVL&&hasVR&&hasHT&&hasHB) break;
        float* line = (float*)cvGetSeqElem(lines,i);
        float rho =  line[0] ;
        float theta = line[1];
        int t = theta*180/PI;

        if( ((t>=0&&t<=20)  ||  (t>=160&&t <=180))&&  fabs(rho)>=5&& fabs(rho)<=w-5)
        {
            if(!hasVL&&!hasVR )
            {
                if( fabsf(rho)<=w/2)
                {
                    line_VL[0] =  rho;
                    line_VL[1] = theta ;
                     hasVL= 1;

                }
                else     if( fabsf(rho)> w/2 &&fabsf(rho)<= w)
                {
                    line_VR[0] =  rho;
                    line_VR[1] = theta ;
                     hasVR= 1;

                }
             }
            else      if(hasVL&&!hasVR )
            {
                 if( fabsf(rho)> w/2 &&fabsf(rho)<= w)
                 {
                     line_VR[0] =  rho;
                     line_VR[1] = theta ;
                     hasVR= 1;

                 }

            }
            else      if(!hasVL&& hasVR )
            {
                if( fabsf(rho)<=w/2)
                {
                    line_VL[0] =  rho;
                    line_VL[1] = theta ;
                    hasVL= 1;

                }
            }
        }
        else    if( t>=70&&t<=110 &&    fabs(rho)>=5&& fabs(rho)<=h-5)
        {
            if(!hasHT  &&   !hasHB )
            {
                if( fabsf(rho)<=h/2)
                {
                    line_HT[0] =  rho;
                    line_HT[1] = theta ;
                     hasHT= 1;
                }
                else     if( fabsf(rho)> h/2 &&fabsf(rho)<= h)
                {
                    line_HB[0] =  rho;
                    line_HB[1] = theta ;
                     hasHB = 1;
                }

            }

            else      if( hasHT  &&    !hasHB )
            {
                if( fabsf(rho)> h/2 &&fabsf(rho)<= h)
                {
                    line_HB[0] =  rho;
                    line_HB[1] = theta ;
                    hasHB = 1;
                }

            }
            else      if( !hasHT  &&  hasHB )
            {
                if( fabsf(rho)<=h/2)
                {
                    line_HT[0] =  rho;
                    line_HT[1] = theta ;
                    hasHT= 1;
                }
            }

        }
       }

    CvPoint line_HT_pt1, line_HT_pt2;
    CvPoint line_HB_pt1, line_HB_pt2;
    CvPoint line_VL_pt1, line_VL_pt2;
    CvPoint line_VR_pt1, line_VR_pt2;

    if(!hasHT)
    {
        line_HT_pt1.x =0;
        line_HT_pt1.y =0;
        line_HT_pt2.x =w;
        line_HT_pt2.y = 0;
        //NSLog(@"line_HT not find");
    }
    else{

       float rho = line_HT[0];
       float theta = line_HT[1];
       double  a = cos(theta);
       double  b = sin(theta);

        double x0 = a*rho;
        double y0 = b*rho;
        line_HT_pt1.x = cvRound(x0 + 1000*(-b));
        line_HT_pt1.y = cvRound(y0 + 1000*(a));
        line_HT_pt2.x = cvRound(x0 - 1000*(-b));
        line_HT_pt2.y = cvRound(y0 - 1000*(a));

    }


    if(!hasHB)
    {

        line_HB_pt1.x = 0;
        line_HB_pt1.y = h;
        line_HB_pt2.x =w;
        line_HB_pt2.y = h;
       // NSLog(@"line_HB not find");

    }
    else{
        float rho = line_HB[0];
        float theta = line_HB[1];
        double a = cos(theta), b = sin(theta);
        double x0 = a*rho, y0 = b*rho;
        line_HB_pt1.x = cvRound(x0 + 1000*(-b));
        line_HB_pt1.y = cvRound(y0 + 1000*(a));
        line_HB_pt2.x = cvRound(x0 - 1000*(-b));
        line_HB_pt2.y = cvRound(y0 - 1000*(a));

    }


    if(!hasVL)
    {
        line_VL_pt1.x = 0;
        line_VL_pt1.y = 0;
        line_VL_pt2.x = 0;
        line_VL_pt2.y = h;
       // NSLog(@"line_VL not find");

    }
    else
    {
        float rho = line_VL[0];
        float theta = line_VL[1];
        double a = cos(theta), b = sin(theta);
        double x0 = a*rho, y0 = b*rho;
        line_VL_pt1.x = cvRound(x0 + 1000*(-b));
        line_VL_pt1.y = cvRound(y0 + 1000*(a));
        line_VL_pt2.x = cvRound(x0 - 1000*(-b));
        line_VL_pt2.y = cvRound(y0 - 1000*(a));

    }


    if(!hasVR)
    {
        line_VR_pt1.x = w;
        line_VR_pt1.y = 0;
        line_VR_pt2.x = w;
        line_VR_pt2.y = h;
        //NSLog(@"line_VR not find");

    }
    else
    {
        float rho = line_VR[0];
        float theta = line_VR[1];
        double a = cos(theta), b = sin(theta);
        double x0 = a*rho, y0 = b*rho;
        line_VR_pt1.x = cvRound(x0 + 1000*(-b));
        line_VR_pt1.y = cvRound(y0 + 1000*(a));
        line_VR_pt2.x = cvRound(x0 - 1000*(-b));
        line_VR_pt2.y = cvRound(y0 - 1000*(a));

    }

    CvPoint _p1= getCrossPointFromTwoLine(line_HT_pt1 ,line_HT_pt2 ,line_VL_pt1 ,line_VL_pt2 );
    CvPoint _p2= getCrossPointFromTwoLine(line_HT_pt1 ,line_HT_pt2 ,line_VR_pt1 ,line_VR_pt2 );
    CvPoint _p3= getCrossPointFromTwoLine(line_HB_pt1 ,line_HB_pt2 ,line_VR_pt1 ,line_VR_pt2 );
    CvPoint _p4= getCrossPointFromTwoLine(line_HB_pt1 ,line_HB_pt2 ,line_VL_pt1 ,line_VL_pt2 );
     float m_xDist = (_p1.x - _p2.x);
    float m_yDist = (_p1.y - _p2.y);
    float m_distance1 = sqrt((m_xDist * m_xDist) + (m_yDist * m_yDist));

    m_xDist = (_p2.x - _p3.x);
    m_yDist = (_p2.y - _p3.y);
    float  m_distance2 = sqrt((m_xDist * m_xDist) + (m_yDist * m_yDist));

    m_xDist = (_p4.x - _p3.x);
    m_yDist = (_p4.y - _p3.y);
    float m_distance3 = sqrt((m_xDist * m_xDist) + (m_yDist * m_yDist));

    m_xDist = (_p1.x - _p4.x);
    m_yDist = (_p1.y - _p4.y);
    float m_distance4  = sqrt((m_xDist * m_xDist) + (m_yDist * m_yDist));

    if(m_distance1<=HOUGH_LINE_MISTAKE_DIST || m_distance2<=HOUGH_LINE_MISTAKE_DIST||m_distance3<=HOUGH_LINE_MISTAKE_DIST||m_distance4<=HOUGH_LINE_MISTAKE_DIST)
    {
        //[tmpHoughArray release];

        //cvReleaseImage( &src );
        cvReleaseImage( &srcimg );
        cvReleaseImage(&color_dst);
        cvReleaseMemStorage(&storage);

        return;
    }





    pt[0].x = _p1.x*1;
    pt[0].y = _p1.y*1;
    pt[1].x = _p2.x*1;
    pt[1].y = _p2.y*1;
    pt[2].x = _p3.x*1;
    pt[2].y = _p3.y*1;
    pt[3].x = _p4.x*1;
    pt[3].y = _p4.y*1;


    //cvReleaseImage( &src );
    cvReleaseImage( &srcimg );
    cvReleaseImage(&color_dst);
    cvReleaseMemStorage(&storage);

}



jintArray Java_com_appxy_tools_LibImgFun_ImgFunInt(JNIEnv *env,
		jclass obj, jstring path) {
	try{

	 const char* jpath = env->GetStringUTFChars(path, NULL);
     if(jpath == NULL){
    	 return NULL;
     }
	 IplImage* img0 = cvLoadImage(jpath , 1);
	 if(img0 == NULL){
		 return NULL;
	 }

     CvPoint* p1;
     CvPoint* p2;
     CvPoint* p3;
     CvPoint* p4;
	 getedgeDetectByImg_Line(img0);

	 cvReleaseImage(&img0);



	jintArray jiArr = env->NewIntArray(8);
	env->SetIntArrayRegion(jiArr, 0, 8, (jint*) pt);
	return jiArr;
	}catch(cv::Exception & e)
	{

		jclass je = env->FindClass("org/opencv/core/CvException");
		if(!je)
		je = env->FindClass("java/lang/Exception");
		env->ThrowNew(je, e.what());
	}
	catch (...)
	{

		jclass je = env->FindClass("java/lang/Exception");
		env->ThrowNew(je, "Unknown exception in JNI code ");
	}

}







IplImage* GetVectorImg_Fast(IplImage* _img,double _s,int* data)
{

    IplImage *src = _img;
    CvPoint2D32f srcQuad[4];

    CvPoint _p1;
    CvPoint _p2;
    CvPoint _p3;
    CvPoint _p4;
    _p1.x = data[0];
    _p1.y = data[1];
    _p2.x = data[2];
    _p2.y = data[3];
    _p3.x = data[4];
    _p3.y = data[5];
    _p4.x = data[6];
    _p4.y = data[7];

    srcQuad[0].x = _p1.x;    srcQuad[0].y = _p1.y;
    srcQuad[1].x = _p2.x;    srcQuad[1].y = _p2.y;
    srcQuad[2].x = _p4.x;    srcQuad[2].y = _p4.y;
    srcQuad[3].x = _p3.x;    srcQuad[3].y = _p3.y;
      double min_y,max_y;
    if(_p1.y<_p2.y) min_y =_p1.y;
    else  min_y =_p2.y;
    if(min_y >_p3.y) min_y = _p3.y;
    if (min_y >_p4.y) {
        min_y = _p4.y;
    }

    if(_p1.y>_p2.y) max_y =_p1.y;
    else  max_y =_p2.y;

    if(max_y <_p3.y) max_y = _p3.y;
    if (max_y <_p4.y) {
        max_y =  _p4.y;
    }

    double min_x,max_x;
    if(_p1.x<_p2.x) min_x =_p1.x;
    else  min_x =_p2.x;
    if(min_x >_p3.x) min_x = _p3.x;
    if (min_x >_p4.x) {
        min_x = _p4.x;
    }

    if(_p1.x>_p2.x) max_x =_p1.x;
    else  max_x =_p2.x;

    if(max_x <_p3.x) max_x = _p3.x;
    if (max_x <_p4.x) {
        max_x =  _p4.x;
    }
    CvPoint2D32f*c1 =(CvPoint2D32f*)malloc(4*sizeof(CvPoint2D32f));
    CvPoint2D32f*c2 =(CvPoint2D32f*)malloc(4*sizeof(CvPoint2D32f));
    c1[0].x = round( srcQuad[0].x*_s);
    c1[0].y = round( srcQuad[0].y*_s);
    c1[1].x = round( srcQuad[1].x*_s);
    c1[1].y = round( srcQuad[1].y*_s);
    c1[2].x = round( srcQuad[2].x*_s);
    c1[2].y = round( srcQuad[2].y*_s);
    c1[3].x = round( srcQuad[3].x*_s);
    c1[3].y = round( srcQuad[3].y*_s);


    c2[0].x = 0;
    c2[0].y = 0;

    c2[1].x = fabsf(max_x - min_x)*_s;
    c2[1].y = 0;

    c2[2].x = 0;
    c2[2].y = fabsf(max_y - min_y)*_s;

    c2[3].x = fabsf(max_x - min_x)*_s;
    c2[3].y = fabsf(max_y - min_y)*_s;

    CvMat* mmat = cvCreateMat(3, 3, CV_32FC1);

    mmat = cvGetPerspectiveTransform(c1, c2, mmat);
    IplImage *dst = cvCreateImage(cvSize( fabsf(max_x - min_x)*_s, fabsf(max_y - min_y)*_s),
                                   src->depth, src->nChannels);
    free(c1);
    free(c2);
    cvWarpPerspective(src, dst, mmat,   CV_INTER_NN+CV_WARP_FILL_OUTLIERS, cvScalarAll(0));
    cvReleaseImage(&src);
    cvReleaseMat(&mmat);

    return dst;



  }


IplImage * change4channelTo3InIplImage(IplImage * src) {
	if (src->nChannels != 4) {
		return NULL;
	}

	IplImage * destImg = cvCreateImage(cvGetSize(src), IPL_DEPTH_8U, 3);
	for (int row = 0; row < src->height; row++) {
		for (int col = 0; col < src->width; col++) {
			CvScalar s = cvGet2D(src, row, col);
			cvSet2D(destImg, row, col, s);
		}
	}

	return destImg;
}

//旋转图像内容不变，尺寸相应变大
IplImage* rotateImage1(IplImage* img,int degree){
	double angle = degree  * CV_PI / 180.; // 弧度
	double a = sin(angle), b = cos(angle);
	int width = img->width;
	int height = img->height;
	int width_rotate= int(height * fabs(a) + width * fabs(b));
	int height_rotate=int(width * fabs(a) + height * fabs(b));
	//旋转数组map
	// [ m0  m1  m2 ] ===>  [ A11  A12   b1 ]
	// [ m3  m4  m5 ] ===>  [ A21  A22   b2 ]
	float map[6];
	CvMat map_matrix = cvMat(2, 3, CV_32F, map);
	// 旋转中心
	CvPoint2D32f center = cvPoint2D32f(width / 2, height / 2);
	cv2DRotationMatrix(center, degree, 1.0, &map_matrix);
	map[2] += (width_rotate - width) / 2;
	map[5] += (height_rotate - height) / 2;
	IplImage* img_rotate = cvCreateImage(cvSize(width_rotate, height_rotate), 8, 3);
	//对图像做仿射变换
	//CV_WARP_FILL_OUTLIERS - 填充所有输出图像的象素。
	//如果部分象素落在输入图像的边界外，那么它们的值设定为 fillval.
	//CV_WARP_INVERSE_MAP - 指定 map_matrix 是输出图像到输入图像的反变换，
	cvWarpAffine( img,img_rotate, &map_matrix, CV_INTER_LINEAR | CV_WARP_FILL_OUTLIERS, cvScalarAll(0));
	return img_rotate;
}



JNIEXPORT void JNICALL Java_com_appxy_tools_LibImgFun_Transfer(
		JNIEnv* env, jobject obj, jstring path, jintArray point, jstring newpath, jint degree)
{

  try{
	const char* jpath = env->GetStringUTFChars(path, NULL);
	const char* jnewpath = env->GetStringUTFChars(newpath, NULL);
	if(path == NULL || newpath == NULL){

	}else{
	int* point2 = env->GetIntArrayElements(point,NULL);

	IplImage* mImg = cvLoadImage(jpath, 1);

	IplImage* mImg2 = NULL;
	 mImg2 =GetVectorImg_Fast(mImg,1,point2);
	int params[3];
	params[0] = CV_IMWRITE_JPEG_QUALITY;
	params[1] = 85;//设置s压缩度
	params[2] = 0;
	IplImage* mImg3 = NULL;

	mImg3 = rotateImage1(mImg2, -degree);
	cvSaveImage(jnewpath , mImg3, params);

	cvReleaseImage(&mImg2);
	cvReleaseImage(&mImg3);
	}
  }catch(cv::Exception & e)
	{

		jclass je = env->FindClass("org/opencv/core/CvException");
		if(!je)
		je = env->FindClass("java/lang/Exception");
		env->ThrowNew(je, e.what());
	}
	catch (...)
	{

		jclass je = env->FindClass("java/lang/Exception");
		env->ThrowNew(je, "Unknown exception in JNI code");
	}

}
