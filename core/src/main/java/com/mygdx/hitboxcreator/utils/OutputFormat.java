package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutputFormat implements Json.Serializable {
    private String name;
    private boolean isDeletable;
    private TypeFormat rectangleFormat;
    private TypeFormat circleFormat;


    @Override
    public void write(Json json) {
        json.writeFields(this);
    }

    /** Custom serializer because json can't handle non-static inner classes such as TypeFormat
     * because actually they don't have a non-arg Constructor even if it seems so. */
    @Override
    public void read(Json json, JsonValue jsonValue) {
        name = jsonValue.getString("name");
        // if boolean has default value 'false', json won't save it
        isDeletable = jsonValue.has("isDeletable");
        rectangleFormat = new TypeFormat(Type.RECTANGLE, jsonValue.get("rectangleFormat").child().asString());
        circleFormat = new TypeFormat(Type.CIRCLE, jsonValue.get("circleFormat").child().asString());
    }

    public OutputFormat() {}

    public OutputFormat(String name, boolean isDeletable) {
        this.name = name;
        this.isDeletable = isDeletable;
        rectangleFormat = new TypeFormat(Type.RECTANGLE);
        circleFormat = new TypeFormat(Type.CIRCLE);
    }

    public OutputFormat(String name, OutputFormat format) {
        this.name = name;
        isDeletable = true;
        rectangleFormat = new TypeFormat(Type.RECTANGLE, format.getMarkup(Type.RECTANGLE));
        circleFormat = new TypeFormat(Type.CIRCLE, format.getMarkup(Type.CIRCLE));
    }

    public boolean isDeletable() { return isDeletable; }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public String getOutputString(Type type, float[] data) {
        switch (type) {
            case CIRCLE:
                return circleFormat.getOutputString(data);
            case RECTANGLE:
                return rectangleFormat.getOutputString(data);

        }
        return "";
    }

    public void setMarkup(Type type, String markup) {
        switch (type) {
            case CIRCLE:
                circleFormat.setMarkup(markup);
                break;
            case RECTANGLE:
                rectangleFormat.setMarkup(markup);
                break;
        }
    }

    public String getMarkup(Type type) {
        switch (type) {
            case CIRCLE:
                return circleFormat.getMarkup();
            case RECTANGLE:
                return rectangleFormat.getMarkup();
        }
        return "Something went wrong...";
    }

    public enum Type {
        CIRCLE,
        RECTANGLE
    }

    class TypeFormat implements Serializable {

        private final transient String SUB = "%s";
        private transient Pattern pattern = Pattern.compile("#[^#]+#");

        private String markup;
        private transient String format;
        private transient ArrayList<String> attributes;
        private transient ArrayList<String> attributeOrder;


        TypeFormat(Type type) {
            this(type, "");
        }

        TypeFormat(Type type, String markup) {
            attributeOrder = new ArrayList<>();
            switch (type) {
                case RECTANGLE:
                    attributes = HitRectangle.attributes;
                    break;
                case CIRCLE:
                    attributes = HitCircle.attributes;
                    break;
            }
            setMarkup(markup);
        }

        /**
         * Call this to set the whole thing up. Has to be called before you can obtain any output.
         *  - sets the markup form
         *  - calls subText() to calculate the formatted form
         *  - and the attribute order
         * @param markup
         */
        void setMarkup(String markup) {
            this.markup = markup;
            format = subText(markup);
        }


        String getMarkup() { return markup; }


        String getOutputString(float[] data) {
            if (markup == null)
                throw new IllegalStateException("Call setMarkup(String) before you can obtain any output.");
            return String.format(format, prepareInput(data));
        }


        /**
         * Replaces each AttributeTag (#ATTRIBUTE#) with "%s" to easily format.
         * Reloads the attribute order.
         * @param text
         * @return
         */
        private String subText(String text) {
            // replace problematic characters
            text = text.replace("%", "%%");
            Matcher matcher = pattern.matcher(text);
            attributeOrder.clear();

            // TODO: "# width=#WIDTH#" hier erkennt er das letzte WIDTH nicht
            while(matcher.find()) {
                String s = text.substring(matcher.start()+1, matcher.end()-1);
                //Gdx.app.log("FOUND",s);
                if (attributes.contains(s)) {
                    //Gdx.app.log("found", s);
                    text = text.replaceFirst(String.format("#%s#",s), SUB);
                    attributeOrder.add(s);
                    matcher.reset(text);
                }
            }
            //Gdx.app.log("format", text);
            return text;
        }

        /**
         * Returns a String array consisting of the ordered HitShape attributes, that we can use to format
         * the output string. We have to cast float values to String because String.format() can't handle
         * a float array as argument.
         *
         * attributesOrder Order in which the HitShape attributes will appear in the output string.
         * Attributes can appear multiple times or not at all.
         *
         * @param data HitShape data obtained from the HitShape.         *
         * @return
         */
        private String[] prepareInput(float[] data) {
            String[] inputData = new String[attributeOrder.size()];

            for (int i = 0; i < attributeOrder.size(); i++) {
                inputData[i] = String.format("%.0f", data[attributes.indexOf(attributeOrder.get(i))]);
            }
            return inputData;
        }
    }
}
