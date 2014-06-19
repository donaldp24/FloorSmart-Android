package com.tim.FloorSmart;

/**
 * Created with IntelliJ IDEA.
 * User: Donald Pae
 * Date: 1/18/14
 * Time: 5:01 PM
 * To change this template use File | SettingsActivity | File Templates.
 */
public class TransformManager {
    // for activity animation ----------------------------------------
    public static int GetContinueInAnim()
    {
        switch(UIManager.transformAnimation)
        {
            case UIManager.TRANSFORM_ALPHA_FADE:
                //return R.anim.trans_in;
                //return R.anim.right_in;
                return R.anim.fade;
            case UIManager.TRANSFORM_TRANSFORM_SLIDE:
                return R.anim.right_in;
            case UIManager.TRANSFORM_ALPHA_SLIDE:
                return R.anim.right_in_alpha;
            default:
                return R.anim.trans_in;
        }
    }
    public static int GetContinueOutAnim()
    {
        switch (UIManager.transformAnimation)
        {
            case UIManager.TRANSFORM_ALPHA_FADE:
                return R.anim.alpha;
                //return R.anim.left_out;
            case UIManager.TRANSFORM_TRANSFORM_SLIDE:
                //return R.anim.left_out;
                return R.anim.alpha;
            case UIManager.TRANSFORM_ALPHA_SLIDE:
                return R.anim.alpha;
            default:
                return R.anim.alpha;
        }
    }

    public static int GetBackInAnim()
    {
        switch (UIManager.transformAnimation)
        {
            case UIManager.TRANSFORM_ALPHA_FADE:
                //return R.anim.trans_out;
                //return R.anim.left_in;
                return R.anim.fade;
            case UIManager.TRANSFORM_TRANSFORM_SLIDE:
                return R.anim.left_in;
            case UIManager.TRANSFORM_ALPHA_SLIDE:
                return R.anim.left_in_alpha;
            default:
                return R.anim.trans_out;
        }
    }

    public static int GetBackOutAnim()
    {
        switch (UIManager.transformAnimation)
        {
            case UIManager.TRANSFORM_ALPHA_FADE:
                return R.anim.alpha;
                //return R.anim.right_out;
            case UIManager.TRANSFORM_TRANSFORM_SLIDE:
                //return R.anim.right_out;
                return R.anim.alpha;
            case UIManager.TRANSFORM_ALPHA_SLIDE:
                return R.anim.alpha;
            default:
                return R.anim.alpha;
        }
    }
    // end for activity animation ---------------------------------------------------

    public static int GetVideoInAnim()
    {
        switch (UIManager.transformAnimation)
        {
            case UIManager.TRANSFORM_ALPHA_FADE:
                return R.anim.video_in;
            default:
                return R.anim.video_in;
        }
    }
    public static int GetVideoOutAnim()
    {
        switch (UIManager.transformAnimation)
        {
            case UIManager.TRANSFORM_ALPHA_FADE:
                return R.anim.video_out;
            default:
                return R.anim.video_out;
        }
    }

    public static int GetDetailViewInAnim()
    {
        //
        return 0;
    }
}
