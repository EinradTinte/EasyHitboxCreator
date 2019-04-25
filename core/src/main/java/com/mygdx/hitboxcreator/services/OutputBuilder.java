package com.mygdx.hitboxcreator.services;

public class OutputBuilder {

    private String text;
    private String rectangleText, circleText;
    private String seperator, rectangleSeperator, circleSeperator;
    private String begin, end;


    private boolean isBuilding = false;
    private OutputFormat outputFormat;



    public OutputBuilder(OutputFormat format) {
        outputFormat = format;
    }


    public void begin() {
        if (isBuilding)
            throw new IllegalStateException("OutputBuilder.end must be called before begin.");

        text = "";
        rectangleText = "";
        circleText = "";

        // TODO: obtain these from outputFormat
        rectangleSeperator = "\n";
        circleSeperator = "\n";
        seperator = "\n";
        begin = "";
        end = "";

        isBuilding = true;
    }

    public void add(OutputFormat.Type type, float[] data) {
        if (!isBuilding)
            throw new IllegalStateException("OutputBuilder.begin must be called before add.");

        String s = outputFormat.getOutputString(type, data);
        switch (type) {
            case RECTANGLE:
                if (!rectangleText.equals("")) rectangleText += rectangleSeperator;
                rectangleText += s;
                break;
            case CIRCLE:
                if (!circleText.equals("")) circleText += circleSeperator;
                circleText += s;
                break;
        }
    }

    public String end() {
        if (!isBuilding)
            throw new IllegalStateException("OutputBuilder.begin must be called before end.");

        isBuilding = false;
        return begin + rectangleText + seperator + circleText + end;
    }

    public void setOutputFormat(OutputFormat outputFormat) {
        if (isBuilding)
            throw new IllegalStateException("OutputBuilder.end must be called before new OutputFormat can be set.");

        this.outputFormat = outputFormat;
    }

    public OutputFormat getOutputFormat() { return outputFormat; }




}
