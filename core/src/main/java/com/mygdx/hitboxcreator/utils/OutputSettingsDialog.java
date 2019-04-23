package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
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
import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.controller.OutputSettingsDialogController;
import com.mygdx.hitboxcreator.services.OutputFormatService;

import java.util.ArrayList;

public class OutputSettingsDialog extends VisDialog implements ActionContainer {

    private final String LML_PATH = "lml/outputSettingsDialogContent.lml";

    OutputFormatService formatService;
    private Stage stage;
    private I18NBundle strings;
    private OutputFormat outputFormat;

    @LmlActor("slboxFormat") SelectBox slboxFormat;
    @LmlActor("htaMarkupRectangle") HighlightTextArea htaMarkupRectangle;
    @LmlActor("htaMarkupCircle") HighlightTextArea htaMarkupCircle;
    @LmlActor("btnOutputFormatNew") VisImageButton btnOutputFormatNew;
    @LmlActor("btnOutputFormatCopy") VisImageButton btnOutputFormatCopy;
    @LmlActor("btnOutputFormatDel") VisImageButton btnOutputFormatDel;

    public OutputSettingsDialog() {
        super("Platzhalter");

        strings = App.inst().getI18NBundle();

        // setting up VisDialog
        this.getTitleLabel().setText(strings.format("dlgTitleOutputSettings"));
        addCloseButton();
        closeOnEscape();
        button(strings.format("buttonOk"), "ok");
        button(strings.format("buttonCancel"), "cancel");
        setResizable(false);

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
        btnOutputFormatNew = (VisImageButton) map.get("btnOutputFormatNew");
        btnOutputFormatCopy = (VisImageButton) map.get("btnOutputFormatCopy");
        btnOutputFormatDel = (VisImageButton) map.get("btnOutputFormatDel");
    }

    private void init() {
        stage = App.inst().getStage();
        strings = App.inst().getI18NBundle();
        formatService = App.inst().getOutputFormatService();
        htaMarkupRectangle.setHighlighter(buildSyntaxHighlighter(Color.CYAN, HitRectangle.attributes));
        htaMarkupCircle.setHighlighter(buildSyntaxHighlighter(Color.CYAN, HitCircle.attributes));
        // TODO: get last selected
        updateSlboxItems();
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
        }
        saveFormats();
    }

    /** Sets the selection to the current format. Gets called on show().
     * Useful when last dialog got closed without setting a new format. */
    @Override
    public VisDialog show(Stage stage) {
        slboxFormat.setSelectedIndex(formatService.getSelectedIndex());
        return super.show(stage);
    }

    @LmlAction("outputSettingsFormatNew") void outputSettingsFormatNew() {
        Dialogs.showInputDialog(stage, strings.format("dlgTitleNewFormat"), strings.format("dlgTextNewFormat"), true, new NameExistsValidator(), new InputDialogListener() {
            @Override
            public void finished(String s) {
                OutputFormat format = new OutputFormat(s, true);
                formatService.add(format);
                updateSlboxItems();
                slboxFormat.setSelectedIndex(slboxFormat.getMaxListCount()+1);
            }

            @Override
            public void canceled() {

            }
        });

    }

    @LmlAction("outputSettingsFormatCopy") void outputSettingsFormatCopy() {
        OutputFormat format = new OutputFormat("platzhalter", formatService.getOutputFormat((String) slboxFormat.getSelected()));
        formatService.add(format);
        updateSlboxItems();

    }


    @LmlAction("outputSettingsFormatDel") void outputSettingsFormatDel() {
        Dialogs.showOptionDialog(stage, strings.format("dlgTitleConfirmDelete"), strings.format("dlgTextConfirmDelete",slboxFormat.getSelected()), Dialogs.OptionDialogType.YES_CANCEL, new OptionDialogListener() {
            @Override
            public void yes() {
                formatService.remove((String) slboxFormat.getSelected());
                updateSlboxItems();
            }

            @Override
            public void no() {

            }

            @Override
            public void cancel() {

            }
        });
    }



    @LmlAction("changeFormatName") void changeFormatName() {
        Dialogs.InputDialog dialog = Dialogs.showInputDialog(stage, strings.format("dlgTitleNewFormat"), strings.format("dlgTextNewFormat"), true, new NameExistsValidator(), new InputDialogListener() {
            @Override
            public void finished(String s) {
                formatService.getOutputFormat(slboxFormat.getSelectedIndex()).setName("replace this");
                formatService.buildFormatNames();
                updateSlboxItems();
            }

            @Override
            public void canceled() {

            }
        });
        dialog.setText(outputFormat.getName(), true);
    }


    /** Updates format on change. */
    @LmlAction("onhtaMarkupChange") void onhtaMarkupChange(HighlightTextArea hta) {
        if (hta.getName().contains("Rectangle")) {
            outputFormat.setMarkup(OutputFormat.Type.RECTANGLE, hta.getText());
        } else if (hta.getName().contains("Circle")) {
            outputFormat.setMarkup(OutputFormat.Type.CIRCLE, hta.getText());
        }
    }





    /** Sets the text in the HighlightTextAreas according to selected format.
     *  ALso disables delete button or writing access.
     */
    @LmlAction("setFormatTexts") void setFormatTexts() {
        OutputFormat format = formatService.getOutputFormat(slboxFormat.getSelectedIndex());
        outputFormat = format;

        htaMarkupRectangle.setText(format.getMarkup(OutputFormat.Type.RECTANGLE));
        htaMarkupCircle.setText(format.getMarkup(OutputFormat.Type.CIRCLE));

        htaMarkupRectangle.setReadOnly(!format.isDeletable());
        htaMarkupCircle.setReadOnly(!format.isDeletable());
        btnOutputFormatDel.setDisabled(!format.isDeletable());
    }



    /** Save OutputSettings onClose */
    private void saveFormats() {
        if (formatService == null) return;
        formatService.saveFormats();
    }

    /** Setup SelectBox Items. */
    private void updateSlboxItems() {
        slboxFormat.setItems(formatService.getFormatNames().toArray());
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
