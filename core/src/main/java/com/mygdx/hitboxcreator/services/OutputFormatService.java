package com.mygdx.hitboxcreator.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mygdx.hitboxcreator.utils.OutputBuilder;
import com.mygdx.hitboxcreator.utils.OutputFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OutputFormatService {
    private final String PREF_NAME = "OutputFormats";

    private OutputBuilder outputBuilder;
    private ArrayList<OutputFormat> outputFormats;
    private List<String> formatNames;
    private String selectedFormat;

    // TODO: use project.OutputFormat when loading a project instead of last used format


    public OutputFormatService() {
        loadFormats();
        int loadIndex = formatNames.contains(selectedFormat) ? formatNames.indexOf(selectedFormat) : 0;
        outputBuilder = new OutputBuilder(outputFormats.get(loadIndex));
    }

    public OutputBuilder getOutputBuilder() { return outputBuilder; }

    /** Find OutputFormat by name. Returns the OutputFormat or null if it does not exist. */
    public OutputFormat getOutputFormat(String name) {
        return formatNames.contains(name) ? outputFormats.get(formatNames.indexOf(name)) : null;
    }

    public OutputFormat getOutputFormat(int index) {
        return index < outputFormats.size() ? outputFormats.get(index) : null;
    }

    public List<String> getFormatNames() { return formatNames; }

    public void setSelectedFormat(String name) {
        if (!formatNames.contains(name))
            throw new IllegalArgumentException(String.format("No OutputFormat with name '%s' found.", name));

        selectedFormat = name;
        outputBuilder.setOutputFormat(outputFormats.get(formatNames.indexOf(name)));
    }

    public OutputFormat getSelectedFormat() { return outputFormats.get(formatNames.indexOf(selectedFormat)); }

    public int getSelectedIndex() { return formatNames.indexOf(selectedFormat); }

    public void add(OutputFormat format) {
        if (exists(format.getName()))
            throw new IllegalArgumentException("An OutputFormat with that name already exists.");

        outputFormats.add(format);
        buildFormatNames();
        saveFormats();
    }

    public void remove(OutputFormat format) {
        if (selectedFormat.equals(format.getName()))
            selectedFormat = formatNames.get(formatNames.indexOf(selectedFormat)-1);
        outputFormats.remove(format);
        buildFormatNames();
        saveFormats();
    }

    public void remove(String name) {
        if (formatNames.contains(name))
            remove(outputFormats.get(formatNames.indexOf(name)));
    }

    public boolean exists(String name) {
        return formatNames.contains(name);
    }

    public void loadFormats() {
        // TODO: try catch block when somebody messed with the file and it's unreadable
        Json json = new Json();
        outputFormats = new ArrayList<>();

        Preferences prefs = Gdx.app.getPreferences(PREF_NAME);
        int count = prefs.getInteger("count", -1);
        // If the file doesn't exist, we create 1 OutputForm manually to use
        if (count < 0) {
            OutputFormat outputFormat = new OutputFormat("XML Format", false);
            outputFormat.setMarkup(OutputFormat.Type.CIRCLE, "<circle x=#X# y=#Y# radius=#RADIUS#/>");
            outputFormat.setMarkup(OutputFormat.Type.RECTANGLE, "<rectangle x=#X# y=#Y# width=#WIDTH# height=#HEIGHT#/>");
            outputFormats.add(outputFormat);
            selectedFormat = outputFormat.getName();
        } else {
            for (int i = 0; i < count; i++) {
                //Gdx.app.log("load format_"+i, prefs.getString("format_"+i));
                outputFormats.add(json.fromJson(OutputFormat.class, prefs.getString("format_"+i)));
            }
            selectedFormat = prefs.getString("selected");
        }

        formatNames = outputFormats.stream().map(OutputFormat::getName).collect(Collectors.toList());
    }

    public void saveFormats() {
        Json json = new Json();
        Preferences prefs = Gdx.app.getPreferences(PREF_NAME);
        prefs.clear();

        prefs.putInteger("count", outputFormats.size());
        prefs.putString("selected", selectedFormat);
        for (int i = 0; i < outputFormats.size(); i++) {
            String formatString = json.toJson(outputFormats.get(i));
            prefs.putString("format_"+i, formatString);
            //Gdx.app.log("save format_"+i, formatString);
        }

        prefs.flush();
    }

    public void buildFormatNames() {
        formatNames.clear();
        formatNames = outputFormats.stream().map(OutputFormat::getName).collect(Collectors.toList());
    }
}
