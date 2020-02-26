/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googy.filters.gpuimage;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.Matrix;

import com.googy.filters.gpuimage.glfilters.GPUImage3x3ConvolutionFilter;
import com.googy.filters.gpuimage.glfilters.GPUImage3x3TextureSamplingFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageAddBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageAlphaBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageBilateralFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageBoxBlurFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageBrightnessFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageBulgeDistortionFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageCGAColorspaceFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageChromaKeyBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageColorBalanceFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageColorBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageColorBurnBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageColorDodgeBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageColorInvertFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageContrastFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageCrosshatchFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageDarkenBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageDifferenceBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageDilationFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageDirectionalSobelEdgeDetectionFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageDissolveBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageDivideBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageEmbossFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageExclusionBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageExposureFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageFalseColorFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageFilterGroup;
import com.googy.filters.gpuimage.glfilters.GPUImageGammaFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageGaussianBlurFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageGlassSphereFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageGrayscaleFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageHalftoneFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageHardLightBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageHazeFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageHighlightShadowFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageHueBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageHueFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageKuwaharaFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageLaplacianFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageLevelsFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageLightenBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageLinearBurnBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageLuminosityBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageMonochromeFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageMultiplyBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageNonMaximumSuppressionFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageNormalBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageOpacityFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageOverlayBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImagePixelationFilter;
import com.googy.filters.gpuimage.glfilters.GPUImagePosterizeFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageRGBDilationFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageRGBFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageSaturationBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageSaturationFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageScreenBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageSepiaFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageSharpenFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageSketchFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageSmoothToonFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageSobelEdgeDetection;
import com.googy.filters.gpuimage.glfilters.GPUImageSoftLightBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageSourceOverBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageSphereRefractionFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageSubtractBlendFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageSwirlFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageToonFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageTransformFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageTwoInputFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageVignetteFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageWeakPixelInclusionFilter;
import com.googy.filters.gpuimage.glfilters.GPUImageWhiteBalanceFilter;

import java.util.LinkedList;
import java.util.List;

public class GPUImageFilterTools {

    public static GPUImageFilter createFilterForType(final Context context, final FilterType type) {
        switch (type) {
            case CONTRAST:
                return new GPUImageContrastFilter(2.0f);
            case GAMMA:
                return new GPUImageGammaFilter(2.0f);
            case INVERT:
                return new GPUImageColorInvertFilter();
            case PIXELATION:
                return new GPUImagePixelationFilter();
            case HUE:
                return new GPUImageHueFilter(90.0f);
            case BRIGHTNESS:
                return new GPUImageBrightnessFilter(0.5f);
            case GRAYSCALE:
                return new GPUImageGrayscaleFilter();
            case SEPIA:
                return new GPUImageSepiaFilter();
            case SHARPEN:
                GPUImageSharpenFilter sharpness = new GPUImageSharpenFilter();
                sharpness.setSharpness(2.0f);
                return sharpness;
            case SOBEL_EDGE_DETECTION:
                return new GPUImageSobelEdgeDetection();
            case THREE_X_THREE_CONVOLUTION:
                GPUImage3x3ConvolutionFilter convolution = new GPUImage3x3ConvolutionFilter();
                convolution.setConvolutionKernel(new float[]{
                        -1.0f, 0.0f, 1.0f,
                        -2.0f, 0.0f, 2.0f,
                        -1.0f, 0.0f, 1.0f
                });
                return convolution;
            case EMBOSS:
                return new GPUImageEmbossFilter();
            case POSTERIZE:
                return new GPUImagePosterizeFilter();
            case FILTER_GROUP:
                List<GPUImageFilter> filters = new LinkedList<GPUImageFilter>();
                filters.add(new GPUImageContrastFilter());
                filters.add(new GPUImageDirectionalSobelEdgeDetectionFilter());
                filters.add(new GPUImageGrayscaleFilter());
                return new GPUImageFilterGroup(filters);
            case SATURATION:
                return new GPUImageSaturationFilter(1.0f);
            case EXPOSURE:
                return new GPUImageExposureFilter(0.0f);
            case HIGHLIGHT_SHADOW:
                return new GPUImageHighlightShadowFilter(0.0f, 1.0f);
            case MONOCHROME:
                return new GPUImageMonochromeFilter(1.0f, new float[]{0.6f, 0.45f, 0.3f, 1.0f});
            case OPACITY:
                return new GPUImageOpacityFilter(1.0f);
            case RGB:
                return new GPUImageRGBFilter(1.0f, 1.0f, 1.0f);
            case WHITE_BALANCE:
                return new GPUImageWhiteBalanceFilter(5000.0f, 0.0f);
            case VIGNETTE:
                PointF centerPoint = new PointF();
                centerPoint.x = 0.5f;
                centerPoint.y = 0.5f;
                return new GPUImageVignetteFilter(centerPoint, new float[]{0.0f, 0.0f, 0.0f}, 0.3f, 0.75f);
            case BLEND_DIFFERENCE:
                return createBlendFilter(context, GPUImageDifferenceBlendFilter.class);
            case BLEND_SOURCE_OVER:
                return createBlendFilter(context, GPUImageSourceOverBlendFilter.class);
            case BLEND_COLOR_BURN:
                return createBlendFilter(context, GPUImageColorBurnBlendFilter.class);
            case BLEND_COLOR_DODGE:
                return createBlendFilter(context, GPUImageColorDodgeBlendFilter.class);
            case BLEND_DARKEN:
                return createBlendFilter(context, GPUImageDarkenBlendFilter.class);
            case BLEND_DISSOLVE:
                return createBlendFilter(context, GPUImageDissolveBlendFilter.class);
            case BLEND_EXCLUSION:
                return createBlendFilter(context, GPUImageExclusionBlendFilter.class);


            case BLEND_HARD_LIGHT:
                return createBlendFilter(context, GPUImageHardLightBlendFilter.class);
            case BLEND_LIGHTEN:
                return createBlendFilter(context, GPUImageLightenBlendFilter.class);
            case BLEND_ADD:
                return createBlendFilter(context, GPUImageAddBlendFilter.class);
            case BLEND_DIVIDE:
                return createBlendFilter(context, GPUImageDivideBlendFilter.class);
            case BLEND_MULTIPLY:
                return createBlendFilter(context, GPUImageMultiplyBlendFilter.class);
            case BLEND_OVERLAY:
                return createBlendFilter(context, GPUImageOverlayBlendFilter.class);
            case BLEND_SCREEN:
                return createBlendFilter(context, GPUImageScreenBlendFilter.class);
            case BLEND_ALPHA:
                return createBlendFilter(context, GPUImageAlphaBlendFilter.class);
            case BLEND_COLOR:
                return createBlendFilter(context, GPUImageColorBlendFilter.class);
            case BLEND_HUE:
                return createBlendFilter(context, GPUImageHueBlendFilter.class);
            case BLEND_SATURATION:
                return createBlendFilter(context, GPUImageSaturationBlendFilter.class);
            case BLEND_LUMINOSITY:
                return createBlendFilter(context, GPUImageLuminosityBlendFilter.class);
            case BLEND_LINEAR_BURN:
                return createBlendFilter(context, GPUImageLinearBurnBlendFilter.class);
            case BLEND_SOFT_LIGHT:
                return createBlendFilter(context, GPUImageSoftLightBlendFilter.class);
            case BLEND_SUBTRACT:
                return createBlendFilter(context, GPUImageSubtractBlendFilter.class);
            case BLEND_CHROMA_KEY:
                return createBlendFilter(context, GPUImageChromaKeyBlendFilter.class);
            case BLEND_NORMAL:
                return createBlendFilter(context, GPUImageNormalBlendFilter.class);
            case GAUSSIAN_BLUR:
                return new GPUImageGaussianBlurFilter();
            case CROSSHATCH:
                return new GPUImageCrosshatchFilter();

            case BOX_BLUR:
                return new GPUImageBoxBlurFilter();
            case CGA_COLORSPACE:
                return new GPUImageCGAColorspaceFilter();
            case DILATION:
                return new GPUImageDilationFilter();
            case KUWAHARA:
                return new GPUImageKuwaharaFilter();
            case RGB_DILATION:
                return new GPUImageRGBDilationFilter();
            case SKETCH:
                return new GPUImageSketchFilter();
            case TOON:
                return new GPUImageToonFilter();
            case SMOOTH_TOON:
                return new GPUImageSmoothToonFilter();

            case BULGE_DISTORTION:
                return new GPUImageBulgeDistortionFilter();
            case GLASS_SPHERE:
                return new GPUImageGlassSphereFilter();
            case HAZE:
                return new GPUImageHazeFilter();
            case LAPLACIAN:
                return new GPUImageLaplacianFilter();
            case NON_MAXIMUM_SUPPRESSION:
                return new GPUImageNonMaximumSuppressionFilter();
            case SPHERE_REFRACTION:
                return new GPUImageSphereRefractionFilter();
            case SWIRL:
                return new GPUImageSwirlFilter();
            case WEAK_PIXEL_INCLUSION:
                return new GPUImageWeakPixelInclusionFilter();
            case FALSE_COLOR:
                return new GPUImageFalseColorFilter();
            case COLOR_BALANCE:
                return new GPUImageColorBalanceFilter();
            case LEVELS_FILTER_MIN:
                GPUImageLevelsFilter levelsFilter = new GPUImageLevelsFilter();
                levelsFilter.setMin(0.0f, 3.0f, 1.0f);
                return levelsFilter;
            case HALFTONE:
                return new GPUImageHalftoneFilter();

            case BILATERAL_BLUR:
                return new GPUImageBilateralFilter();

            case TRANSFORM2D:
                return new GPUImageTransformFilter();
            case NOFILTER:
                return new GPUImageFilter();
            default:
                throw new IllegalStateException("No filter of that type! " + type.name());
        }

    }

    private static GPUImageFilter createBlendFilter(Context context, Class<? extends GPUImageTwoInputFilter> filterClass) {
        try {
            GPUImageTwoInputFilter filter = filterClass.newInstance();
//            filter.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
            return filter;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum FilterType {
        NOFILTER, CONTRAST, GRAYSCALE, SHARPEN, SEPIA, SOBEL_EDGE_DETECTION, THREE_X_THREE_CONVOLUTION, FILTER_GROUP, EMBOSS, POSTERIZE, GAMMA, BRIGHTNESS, INVERT, HUE, PIXELATION,
        SATURATION, EXPOSURE, HIGHLIGHT_SHADOW, MONOCHROME, OPACITY, RGB, WHITE_BALANCE, VIGNETTE, TONE_CURVE, BLEND_COLOR_BURN, BLEND_COLOR_DODGE, BLEND_DARKEN, BLEND_DIFFERENCE,
        BLEND_DISSOLVE, BLEND_EXCLUSION, BLEND_SOURCE_OVER, BLEND_HARD_LIGHT, BLEND_LIGHTEN, BLEND_ADD, BLEND_DIVIDE, BLEND_MULTIPLY, BLEND_OVERLAY, BLEND_SCREEN, BLEND_ALPHA,
        BLEND_COLOR, BLEND_HUE, BLEND_SATURATION, BLEND_LUMINOSITY, BLEND_LINEAR_BURN, BLEND_SOFT_LIGHT, BLEND_SUBTRACT, BLEND_CHROMA_KEY, BLEND_NORMAL, LOOKUP_AMATORKA,
        GAUSSIAN_BLUR, CROSSHATCH, BOX_BLUR, CGA_COLORSPACE, DILATION, KUWAHARA, RGB_DILATION, SKETCH, TOON, SMOOTH_TOON, BULGE_DISTORTION, GLASS_SPHERE, HAZE, LAPLACIAN, NON_MAXIMUM_SUPPRESSION,
        SPHERE_REFRACTION, SWIRL, WEAK_PIXEL_INCLUSION, FALSE_COLOR, COLOR_BALANCE, LEVELS_FILTER_MIN, BILATERAL_BLUR, HALFTONE, TRANSFORM2D
    }

    private static class FilterList {
        public List<String> names = new LinkedList<String>();
        public List<FilterType> filters = new LinkedList<FilterType>();

        public void addFilter(final String name, final FilterType filter) {
            names.add(name);
            filters.add(filter);
        }
    }

    public static class FilterAdjuster {
        private final Adjuster<? extends GPUImageFilter> adjuster;

        public FilterAdjuster(final GPUImageFilter filter) {
            if (filter instanceof GPUImageSharpenFilter) {
                adjuster = new SharpnessAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSepiaFilter) {
                adjuster = new SepiaAdjuster().filter(filter);
            } else if (filter instanceof GPUImageContrastFilter) {
                adjuster = new ContrastAdjuster().filter(filter);
            } else if (filter instanceof GPUImageGammaFilter) {
                adjuster = new GammaAdjuster().filter(filter);
            } else if (filter instanceof GPUImageBrightnessFilter) {
                adjuster = new BrightnessAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSobelEdgeDetection) {
                adjuster = new SobelAdjuster().filter(filter);
            } else if (filter instanceof GPUImageEmbossFilter) {
                adjuster = new EmbossAdjuster().filter(filter);
            } else if (filter instanceof GPUImage3x3TextureSamplingFilter) {
                adjuster = new GPU3x3TextureAdjuster().filter(filter);
            } else if (filter instanceof GPUImageHueFilter) {
                adjuster = new HueAdjuster().filter(filter);
            } else if (filter instanceof GPUImagePosterizeFilter) {
                adjuster = new PosterizeAdjuster().filter(filter);
            } else if (filter instanceof GPUImagePixelationFilter) {
                adjuster = new PixelationAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSaturationFilter) {
                adjuster = new SaturationAdjuster().filter(filter);
            } else if (filter instanceof GPUImageExposureFilter) {
                adjuster = new ExposureAdjuster().filter(filter);
            } else if (filter instanceof GPUImageHighlightShadowFilter) {
                adjuster = new HighlightShadowAdjuster().filter(filter);
            } else if (filter instanceof GPUImageMonochromeFilter) {
                adjuster = new MonochromeAdjuster().filter(filter);
            } else if (filter instanceof GPUImageOpacityFilter) {
                adjuster = new OpacityAdjuster().filter(filter);
            } else if (filter instanceof GPUImageRGBFilter) {
                adjuster = new RGBAdjuster().filter(filter);
            } else if (filter instanceof GPUImageWhiteBalanceFilter) {
                adjuster = new WhiteBalanceAdjuster().filter(filter);
            } else if (filter instanceof GPUImageVignetteFilter) {
                adjuster = new VignetteAdjuster().filter(filter);
            } else if (filter instanceof GPUImageDissolveBlendFilter) {
                adjuster = new DissolveBlendAdjuster().filter(filter);
            } else if (filter instanceof GPUImageGaussianBlurFilter) {
                adjuster = new GaussianBlurAdjuster().filter(filter);
            } else if (filter instanceof GPUImageCrosshatchFilter) {
                adjuster = new CrosshatchBlurAdjuster().filter(filter);
            } else if (filter instanceof GPUImageBulgeDistortionFilter) {
                adjuster = new BulgeDistortionAdjuster().filter(filter);
            } else if (filter instanceof GPUImageGlassSphereFilter) {
                adjuster = new GlassSphereAdjuster().filter(filter);
            } else if (filter instanceof GPUImageHazeFilter) {
                adjuster = new HazeAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSphereRefractionFilter) {
                adjuster = new SphereRefractionAdjuster().filter(filter);
            } else if (filter instanceof GPUImageSwirlFilter) {
                adjuster = new SwirlAdjuster().filter(filter);
            } else if (filter instanceof GPUImageColorBalanceFilter) {
                adjuster = new ColorBalanceAdjuster().filter(filter);
            } else if (filter instanceof GPUImageLevelsFilter) {
                adjuster = new LevelsMinMidAdjuster().filter(filter);
            } else if (filter instanceof GPUImageBilateralFilter) {
                adjuster = new BilateralAdjuster().filter(filter);
            } else if (filter instanceof GPUImageTransformFilter) {
                adjuster = new RotateAdjuster().filter(filter);
            } else {

                adjuster = null;
            }
        }

        public boolean canAdjust() {
            return adjuster != null;
        }

        public void adjust(final float percentage) {
            if (adjuster != null) {
                adjuster.adjust(percentage);
            }
        }

        public Adjuster<? extends GPUImageFilter> getAdjuster() {
            return adjuster;
        }

        public abstract class Adjuster<T extends GPUImageFilter> {
            private T filter;

            @SuppressWarnings("unchecked")
            public Adjuster<T> filter(final GPUImageFilter filter) {
                this.filter = (T) filter;
                return this;
            }

            public T getFilter() {
                return filter;
            }

            public abstract void adjust(float percentage);

            public abstract float getPercentage();

            protected float range(final int percentage, final float start, final float end) {
                return (end - start) * percentage / 1000.0f + start;
            }

            protected int range(final int percentage, final int start, final int end) {
                return (end - start) * percentage / 1000 + start;
            }

            protected float range(final float percentage, final float start, final float end) {
                return (end - start) * percentage / 1000f + start;
            }

            protected int reverse(final float value, final float start, final float end) {
                return (int) (((1000 + start) * value) / (start + end));
            }

            protected int reverse(final int value, final int start, final int end) {
                return ((1000 + start) * value) / (start + end);
            }
        }

        private class SharpnessAdjuster extends Adjuster<GPUImageSharpenFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setSharpness(range(percentage, -4.0f, 4.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getSharpness(), -4.0f, 4.0f);
            }
        }

        private class PixelationAdjuster extends Adjuster<GPUImagePixelationFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setPixel(range(percentage, 1.0f, 100.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getPixel(), 1.0f, 100.0f);
            }
        }

        private class HueAdjuster extends Adjuster<GPUImageHueFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setHue(range(percentage, 0.0f, 360.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getHue(), 0.0f, 360.0f);
            }
        }

        private class ContrastAdjuster extends Adjuster<GPUImageContrastFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setContrast(range(percentage, 0.0f, 2.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getContrast(), 0.0f, 2.0f);
            }
        }

        private class GammaAdjuster extends Adjuster<GPUImageGammaFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setGamma(range(percentage, 0.0f, 3.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getGamma(), 0.0f, 3.0f);
            }
        }

        private class BrightnessAdjuster extends Adjuster<GPUImageBrightnessFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setBrightness(range(percentage, -1.0f, 1.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getBrightness(), -1.0f, 1.0f);
            }
        }

        private class SepiaAdjuster extends Adjuster<GPUImageSepiaFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setIntensity(range(percentage, 0.0f, 2.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getIntensity(), 0.0f, 2.0f);
            }
        }

        private class SobelAdjuster extends Adjuster<GPUImageSobelEdgeDetection> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setLineSize(range(percentage, 0.0f, 5.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(((GPUImage3x3TextureSamplingFilter) getFilter().getFilters().get(1)).getLineSize(), 0.0f, 5.0f);
            }
        }

        private class EmbossAdjuster extends Adjuster<GPUImageEmbossFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setIntensity(range(percentage, 0.0f, 4.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getIntensity(), 0.0f, 4.0f);
            }
        }

        private class PosterizeAdjuster extends Adjuster<GPUImagePosterizeFilter> {
            @Override
            public void adjust(final float percentage) {
                // In theorie to 256, but only first 50 are interesting
                getFilter().setColorLevels(range(percentage, 1, 50));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getColorLevels(), 1, 50);
            }
        }

        private class GPU3x3TextureAdjuster extends Adjuster<GPUImage3x3TextureSamplingFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setLineSize(range(percentage, 0.0f, 5.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getLineSize(), 0.0f, 5.0f);
            }
        }

        private class SaturationAdjuster extends Adjuster<GPUImageSaturationFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setSaturation(range(percentage, 0.0f, 2.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getSaturation(), 0.0f, 2.0f);
            }
        }

        private class ExposureAdjuster extends Adjuster<GPUImageExposureFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setExposure(range(percentage, -10.0f, 10.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getExposure(), -10.0f, 10.0f);
            }
        }

        private class HighlightShadowAdjuster extends Adjuster<GPUImageHighlightShadowFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setShadows(range(percentage, 0.0f, 1.0f));
                getFilter().setHighlights(range(percentage, 0.0f, 1.0f));
            }

            @Override
            public float getPercentage() {
                return 0;
            }
        }

        private class MonochromeAdjuster extends Adjuster<GPUImageMonochromeFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setIntensity(range(percentage, 0.0f, 1.0f));
                //getFilter().setColor(new float[]{0.6f, 0.45f, 0.3f, 1.0f});
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getIntensity(), 0.0f, 1.0f);
            }
        }

        private class OpacityAdjuster extends Adjuster<GPUImageOpacityFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setOpacity(range(percentage, 0.0f, 1.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getOpacity(), 0.0f, 1.0f);
            }
        }

        private class RGBAdjuster extends Adjuster<GPUImageRGBFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setRed(range(percentage, 0.0f, 1.0f));
                getFilter().setGreen(range(percentage, 0.0f, 1.0f));
                getFilter().setBlue(range(percentage, 0.0f, 1.0f));
            }

            @Override
            public float getPercentage() {
                return 0;
            }
        }

        private class WhiteBalanceAdjuster extends Adjuster<GPUImageWhiteBalanceFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setTemperature(range(percentage, 2000.0f, 8000.0f));
                //getFilter().setTint(range(percentage, -100.0f, 100.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getTemperature(), 2000.0f, 8000.0f);
            }
        }

        private class VignetteAdjuster extends Adjuster<GPUImageVignetteFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setVignetteStart(range(percentage, 0.0f, 1.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getVignetteStart(), 0.0f, 1.0f);
            }
        }

        private class DissolveBlendAdjuster extends Adjuster<GPUImageDissolveBlendFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setMix(range(percentage, 0.0f, 1.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getMix(), 0.0f, 1.0f);
            }
        }

        private class GaussianBlurAdjuster extends Adjuster<GPUImageGaussianBlurFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setBlurSize(range(percentage, 0.0f, 1.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getBlurSize(), 0.0f, 1.0f);
            }
        }

        private class CrosshatchBlurAdjuster extends Adjuster<GPUImageCrosshatchFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setCrossHatchSpacing(range(percentage, 0.0f, 0.06f));
                getFilter().setLineWidth(range(percentage, 0.0f, 0.006f));
            }

            @Override
            public float getPercentage() {
                return 0;
            }
        }

        private class BulgeDistortionAdjuster extends Adjuster<GPUImageBulgeDistortionFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setRadius(range(percentage, 0.0f, 1.0f));
                getFilter().setScale(range(percentage, -1.0f, 1.0f));
            }

            @Override
            public float getPercentage() {
                return 0;
            }
        }

        private class GlassSphereAdjuster extends Adjuster<GPUImageGlassSphereFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setRadius(range(percentage, 0.0f, 1.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getRadius(), 0.0f, 1.0f);
            }
        }

        private class HazeAdjuster extends Adjuster<GPUImageHazeFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setDistance(range(percentage, -0.3f, 0.3f));
                getFilter().setSlope(range(percentage, -0.3f, 0.3f));
            }

            @Override
            public float getPercentage() {
                return 0;
            }
        }

        private class SphereRefractionAdjuster extends Adjuster<GPUImageSphereRefractionFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setRadius(range(percentage, 0.0f, 1.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getRadius(), 0.0f, 1.0f);
            }
        }

        private class SwirlAdjuster extends Adjuster<GPUImageSwirlFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setAngle(range(percentage, 0.0f, 2.0f));
            }

            @Override
            public float getPercentage() {
                return reverse(getFilter().getAngle(), 0.0f, 2.0f);
            }
        }

        private class ColorBalanceAdjuster extends Adjuster<GPUImageColorBalanceFilter> {

            @Override
            public void adjust(float percentage) {
                getFilter().setMidtones(new float[]{
                        range(percentage, 0.0f, 1.0f),
                        range(percentage / 2, 0.0f, 1.0f),
                        range(percentage / 3, 0.0f, 1.0f)});
            }

            @Override
            public float getPercentage() {
                return 0;
            }
        }

        private class LevelsMinMidAdjuster extends Adjuster<GPUImageLevelsFilter> {
            @Override
            public void adjust(float percentage) {
                getFilter().setMin(0.0f, range(percentage, 0.0f, 1.0f), 1.0f);
            }

            @Override
            public float getPercentage() {
                return 0;
            }
        }

        private class BilateralAdjuster extends Adjuster<GPUImageBilateralFilter> {
            @Override
            public void adjust(final float percentage) {
                getFilter().setDistanceNormalizationFactor(range(percentage, 0.0f, 15.0f));
            }

            @Override
            public float getPercentage() {
                return 0;
            }
        }

        private class RotateAdjuster extends Adjuster<GPUImageTransformFilter> {
            @Override
            public void adjust(final float angel) {
                float[] transform = new float[16];
                Matrix.setRotateM(transform, 0, angel, 0, 0, 1.0f);
                getFilter().setTransform3D(transform);
            }

            @Override
            public float getPercentage() {
                return 0;
            }
        }

    }
}
