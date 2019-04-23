package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutputBuilder {

    private String text;
    private boolean isBuilding = false;
    private OutputFormat outputFormat;

    public OutputBuilder(OutputFormat format) {
        outputFormat = format;
    }


    public void begin() {
        if (isBuilding)
            throw new IllegalStateException("OutputBuilder.end must be called before begin.");

        text = "";
        isBuilding = true;
    }

    public void add(OutputFormat.Type type, float[] data) {
        if (!isBuilding)
            throw new IllegalStateException("OutputBuilder.begin must be called before add.");

        text += outputFormat.getOutputString(type, data) +'\n';
    }

    public String end() {
        if (!isBuilding)
            throw new IllegalStateException("OutputBuilder.begin must be called before end.");

        isBuilding = false;
        return text;
    }

    public void setOutputFormat(OutputFormat outputFormat) {
        if (isBuilding)
            throw new IllegalStateException("OutputBuilder.end must be called before new OutputFormat can be set.");

        this.outputFormat = outputFormat;
    }

    public OutputFormat getOutputFormat() { return outputFormat; }




}
