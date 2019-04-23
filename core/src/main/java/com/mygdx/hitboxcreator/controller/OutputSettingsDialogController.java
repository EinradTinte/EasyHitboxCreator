package com.mygdx.hitboxcreator.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.utils.I18NBundle;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.annotation.LmlAfter;
import com.github.czyzby.lml.annotation.LmlBefore;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.InputDialogListener;
import com.kotcrab.vis.ui.util.dialog.OptionDialogListener;
import com.kotcrab.vis.ui.util.highlight.BaseHighlighter;
import com.kotcrab.vis.ui.widget.HighlightTextArea;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.utils.HitCircle;
import com.mygdx.hitboxcreator.utils.HitRectangle;
import com.mygdx.hitboxcreator.utils.OutputFormat;
import com.mygdx.hitboxcreator.services.OutputFormatService;

import java.util.ArrayList;

public class OutputSettingsDialogController extends AbstractLmlView {

    OutputFormatService formatService;
    private Stage stage;
    private I18NBundle strings;
    private OutputFormat outputFormat;

    // TODO: i18l strings


    @LmlActor("slboxFormat") SelectBox slboxFormat;
    @LmlActor("htaMarkupRectangle") HighlightTextArea htaMarkupRectangle;
    @LmlActor("htaMarkupCircle") HighlightTextArea htaMarkupCircle;
    @LmlActor("btnOutputFormatNew") VisImageButton btnOutputFormatNew;
    @LmlActor("btnOutputFormatCopy") VisImageButton btnOutputFormatCopy;
    @LmlActor("btnOutputFormatDel") VisImageButton btnOutputFormatDel;


    public OutputSettingsDialogController() {
        super(null);
    }


    @Override
    public String getViewId() {
        return "OutputSettingsDialogController";
    }




    @LmlAfter void initView() {
        stage = App.inst().getStage();
        strings = App.inst().getI18NBundle();
        formatService = App.inst().getOutputFormatService();
        htaMarkupRectangle.setHighlighter(buildSyntaxHighlighter(Color.CYAN, HitRectangle.attributes));
        htaMarkupCircle.setHighlighter(buildSyntaxHighlighter(Color.CYAN, HitCircle.attributes));
        // TODO: get last selected
        updateSlboxItems();
        setFormatTexts();
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
                formatService.setSelectedFormat((String) slboxFormat.getSelected());
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

    @LmlAction("onOk") boolean setFormat() {
        formatService.setSelectedFormat((String) slboxFormat.getSelected());
        // TODO: this has to trigger the OutputPane to update
        // return false so that dialog closes after click on ok
        return false;
    }

    /** Save OutputSettings onClose */
    @LmlAction("saveFormats") void saveFormats() {
        if (formatService == null) return;
        formatService.saveFormats();
    }

    /** Setup SelectBox Items. */
    private void updateSlboxItems() {
        slboxFormat.setItems(formatService.getFormatNames().toArray());
    }


    /** Sets the selection to the current format. Gets called on show().
     * Useful when last dialog got closed without setting a new format. */
    @LmlAction("setSlBoxSelected") void setSlBoxSelected() {
        if (slboxFormat == null) return;
        slboxFormat.setSelectedIndex(formatService.getSelectedIndex());
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
