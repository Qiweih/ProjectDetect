package com.beacon.projectdetect.Adapter;

import android.app.Application;
import android.graphics.Typeface;

/**
 * Created by qiwhuang on 4/7/2017.
 */

public class TypeManager {

        private static final String FONTS_KEEP_CALM_MEDIUM_TTF = "fonts/KeepCalm-Medium.ttf";
        private static final String FONTS_RALEWAY_BOLD_TTF = "fonts/Raleway-Bold.ttf";
        private static final String FONTS_RALEWAY_ITALIC_TTF = "fonts/Raleway-Italic.ttf";
        private static final String FONTS_RALEWAY_REGULAR_TTF = "fonts/Raleway-Regular.ttf";
        private static final String FONTS_RALEWAY_SEMI_BOLD_TTF = "fonts/Raleway-SemiBold.ttf";

        private static TypeManager instance;

        private Typeface typefaceKeepCalmMedium;
        private Typeface typefaceRalewayBold;
        private Typeface typefaceRalewayItalic;
        private Typeface typefaceRalewayRegular;
        private Typeface typefaceRalewaySemibold;

        private TypeManager(Application appContext) {
        }

        public static void createInstance(Application appContext) {
            instance = new TypeManager(appContext);
        }

        public static TypeManager getInstance() {
            if (instance != null) {
                return instance;
            } else {
                throw new RuntimeException("instance has not been initialized by application");
            }
        }

        public Typeface getTypefaceKeepCalmMedium() {
            return typefaceKeepCalmMedium;
        }

        public Typeface getTypefaceRalewayBold() {
            return typefaceRalewayBold;
        }

        public Typeface getTypefaceRalewayItalic() {
            return typefaceRalewayItalic;
        }

        public Typeface getTypefaceRalewayRegular() {
            return typefaceRalewayRegular;
        }

        public Typeface getTypefaceRalewaySemibold() {
            return typefaceRalewaySemibold;
        }

}
