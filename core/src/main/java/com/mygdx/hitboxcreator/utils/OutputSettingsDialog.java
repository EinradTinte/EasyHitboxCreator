package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Timer;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.annotation.LmlAfter;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.InputDialogListener;
import com.kotcrab.vis.ui.util.dialog.OptionDialogListener;
import com.kotcrab.vis.ui.util.highlight.BaseHighlighter;
import com.kotcrab.vis.ui.widget.HighlightTextArea;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.controller.OutputSettingsDialogController;
import com.mygdx.hitboxcreator.events.HitShapesChangedEvent;
import com.mygdx.hitboxcreator.services.OutputFormatService;

import java.util.ArrayList;

public class OutputSettingsDialog extends VisDialog implements ActionContainer {

    private final String LML_PATH = "lml/outputSettingsDialogContent.lml";
    private final float[] rec1preview = new float[] {0, 20, 100, 100};
    private final float[] rec2preview = new float[] {20, 15, 120, 30};
    private final float[] cir1preview = new float[] {70, 25, 30};
    private final float[] cir2preview = new float[] {50, 50, 18};

    private OutputFormatService formatService;
    private OutputBuilder outputBuilder;
    private Stage stage;
    private I18NBundle strings;
    private OutputFormat outputFormat;

    @LmlActor("slboxFormat") private SelectBox slboxFormat;
    @LmlActor("htaMarkupRectangle") private HighlightTextArea htaMarkupRectangle;
    @LmlActor("htaMarkupCircle") private HighlightTextArea htaMarkupCircle;
    @LmlActor("btnOutputFormatRename") private VisImageButton btnOutputFormatRename;
    @LmlActor("btnOutputFormatDel") private VisImageButton btnOutputFormatDel;
    @LmlActor("taMarkupPreview") private VisLabel taMarkupPreview;

    public OutputSettingsDialog() {
        super("");

        strings = App.inst().getI18NBundle();

        // setting up VisDialog
        this.getTitleLabel().setText(strings.format("dlgTitleOutputSettings"));
        addCloseButton();
        closeOnEscape();
        button(strings.format("buttonOk"), "ok");
        button(strings.format("buttonCancel"), "cancel");
        setResizable(false);
        getContentTable().align(Align.left);


        // Parsing the content
        LmlParser parser = App.inst().getParser();
        parser.getData().addActionContainer("OutputSettingsDialogActions", this);
        getContentTable().add(parser.parseTemplate(Gdx.files.internal(LML_PATH)).first());
        //getContentTable().setFillParent(true);
        mapActors(parser.getActorsMappedByIds());
        init();
    }

    private void mapActors(ObjectMap<String, Actor> map) {
        slboxFormat = (SelectBox) map.get("slboxFormat");
        htaMarkupRectangle = (HighlightTextArea) map.get("htaMarkupRectangle");
        htaMarkupCircle = (HighlightTextArea) map.get("htaMarkupCircle");
        btnOutputFormatRename = (VisImageButton) map.get("btnOutputFormatRename");
        btnOutputFormatDel = (VisImageButton) map.get("btnOutputFormatDel");
        taMarkupPreview = (VisLabel) map.get("taMarkupPreview");
    }

    private void init() {
        stage = App.inst().getStage();
        strings = App.inst().getI18NBundle();
        formatService = App.inst().getOutputFormatService();
        outputBuilder = new OutputBuilder(formatService.getOutputFormat(0));
        htaMarkupRectangle.setHighlighter(buildSyntaxHighlighter(Color.CYAN, HitRectangle.attributes));
        htaMarkupCircle.setHighlighter(buildSyntaxHighlighter(Color.CYAN, HitCircle.attributes));
        updateSlboxItems(formatService.getSelectedIndex());
        setFormatTexts();
    }

    /** Save formats when dialog gets closed by close button or onEscape. */
    @Override
    protected void close() {
        saveFormats();
        fadeOut();
    }

    /** Save formats when dialog gets closed by a button in the button table. */
    @Override
    protected void result(Object object) {
        if ((object).equals("ok")) {
            formatService.setSelectedFormat((String) slboxFormat.getSelected());
            App.inst().getEventDispatcher().postEvent(new HitShapesChangedEvent(HitShapesChangedEvent.Action.FORM_CHANGED));
        }
        saveFormats();
    }

    /** Sets the selection to the current format. Gets called on show().
     * Useful when last dialog got closed without setting a new format. */
    @Override
    public VisDialog show(Stage stage) {
        // hack to circumvent vistextareas string visibilty bug. Normally the setFormatTexts() method
        // takes care of this but for some reason calling it before super.show() has no effect or gets reverted.
        // So we have to do this in a Timer.Task. DelayTime can be set to 0.
        // Fucking weird behavior. That cost me so much time.
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                slboxFormat.setSelectedIndex(formatService.getSelectedIndex());
                // if selection is the same as before, setFormatTexts() will not be automatically called so we do it
                setFormatTexts();
            }
        },0);

        return super.show(stage);
    }

    @LmlAction("outputSettingsFormatNew") void outputSettingsFormatNew() {
        Dialogs.showInputDialog(stage, strings.format("dlgTextChooseName"), null, false, new NameExistsValidator(), new InputDialogListener() {
            @Override
            public void finished(String s) {
                OutputFormat format = new OutputFormat(s, true);
                formatService.add(format);
                updateSlboxItems(slboxFormat.getItems().size);
            }

            @Override
            public void canceled() {

            }
        });
    }


    @LmlAction("outputSettingsFormatCopy") void outputSettingsFormatCopy() {

        Dialogs.showInputDialog(stage, strings.format("dlgTextChooseName"), null, false, new NameExistsValidator(), new InputDialogListener() {
            @Override
            public void finished(String s) {
                OutputFormat format = new OutputFormat(s, formatService.getOutputFormat((String) slboxFormat.getSelected()));
                formatService.add(format);
                updateSlboxItems(slboxFormat.getItems().size);
            }

            @Override
            public void canceled() {

            }
        }).setText(String.format("%s %s", outputFormat.getName(), strings.format("dlgTextCopy")), true);
    }


    @LmlAction("outputSettingsFormatRename") void changeFormatName() {
        Dialogs.showInputDialog(stage, strings.format("dlgTextChooseName"), null, false, new NameExistsValidator(), new InputDialogListener() {
            @Override
            public void finished(String s) {
                formatService.getOutputFormat(slboxFormat.getSelectedIndex()).setName(s);
                formatService.buildFormatNames();
                saveFormats();
                updateSlboxItems(slboxFormat.getSelectedIndex());
            }

            @Override
            public void canceled() {

            }
        }).setText(outputFormat.getName(), true);
    }


    @LmlAction("outputSettingsFormatDel") void outputSettingsFormatDel() {
        Dialogs.showOptionDialog(stage, strings.format("dlgTitleConfirmDelete"), strings.format("dlgTextConfirmDelete",slboxFormat.getSelected()), Dialogs.OptionDialogType.YES_CANCEL, new OptionDialogListener() {
            @Override
            public void yes() {
                formatService.remove((String) slboxFormat.getSelected());
                updateSlboxItems(slboxFormat.getSelectedIndex()-1);
            }

            @Override
            public void no() {

            }

            @Override
            public void cancel() {

            }
        });
    }



    /** Updates format on change. */
    @LmlAction("onhtaMarkupChange") void onhtaMarkupChange(HighlightTextArea hta) {

        if (hta.getName().contains("Rectangle")) {
            outputFormat.setMarkup(OutputFormat.Type.RECTANGLE, hta.getText());
        } else if (hta.getName().contains("Circle")) {
            outputFormat.setMarkup(OutputFormat.Type.CIRCLE, hta.getText());
        }
        setTextPreview();
    }

    /** Updating the preview text. */
    private void setTextPreview() {
        outputBuilder.begin();

        outputBuilder.add(OutputFormat.Type.RECTANGLE, rec1preview);
        outputBuilder.add(OutputFormat.Type.RECTANGLE, rec2preview);
        outputBuilder.add(OutputFormat.Type.CIRCLE, cir1preview);
        outputBuilder.add(OutputFormat.Type.CIRCLE, cir2preview);

        taMarkupPreview.setText(outputBuilder.end());
    }


    /** Sets the text in the HighlightTextAreas according to selected format.
     *  ALso disables delete button or writing access.
     */
    @LmlAction("setFormatTexts") void setFormatTexts() {
        OutputFormat format = formatService.getOutputFormat(slboxFormat.getSelectedIndex());
        outputFormat = format;

        htaMarkupRectangle.setText(format.getMarkup(OutputFormat.Type.RECTANGLE) +" ");
        htaMarkupCircle.setText(format.getMarkup(OutputFormat.Type.CIRCLE) +" ");

        outputBuilder.setOutputFormat(format);
        // hack to fix text not fully shown when to wide


        stage.draw();

        htaMarkupRectangle.setText(format.getMarkup(OutputFormat.Type.RECTANGLE));
        htaMarkupCircle.setText(format.getMarkup(OutputFormat.Type.CIRCLE));



        setTextPreview();

        htaMarkupRectangle.setReadOnly(!format.isDeletable());
        htaMarkupCircle.setReadOnly(!format.isDeletable());
        btnOutputFormatRename.setDisabled(!format.isDeletable());
        btnOutputFormatDel.setDisabled(!format.isDeletable());
    }



    /** Save OutputSettings onClose */
    private void saveFormats() {
        if (formatService == null) return;
        formatService.saveFormats();
    }

    /** Setup SelectBox Items. */
    private void updateSlboxItems(int selectedIndex) {
        slboxFormat.setItems(formatService.getFormatNames().toArray());
        slboxFormat.setSelectedIndex(selectedIndex);
    }






    /** Returns a Highlighter that colors every committed word enclosed in '#'.
     * i.e. WIDTH -> #WIDTH#
     */
    private BaseHighlighter buildSyntaxHighlighter(Color color, ArrayList<String> words) {
        BaseHighlighter highlighter = new BaseHighlighter();
        for (String word : words) {
            highlighter.word(color, '#'+word+'#');
        }
        return highlighter;
    }

    /** Validator that invalidates when String is empty or a format with such a name already exists. */
    class NameExistsValidator implements InputValidator {
        @Override
        public boolean validateInput(String s) {
            if (s.equals("")) return false;
            return !formatService.exists(s);
        }
    }
}
