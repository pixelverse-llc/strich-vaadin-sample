package io.strich.samples.vaadin.views.scanning;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.ui.LoadMode;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Multiple Scans")
@Route("scan-continuous")
@JavaScript(value = "https://cdn.jsdelivr.net/npm/@pixelverse/strichjs-sdk@1.13.0/dist/strich-noesm.js", loadMode = LoadMode.EAGER)
public class ScanMultipleView extends VerticalLayout {

    private static final int NUM_BARCODES = 4;
    private List<String> barcodes;
    private Div barcodeLabels[];

    public ScanMultipleView() {
        add(new H2("Multiple Scans"));

        // STRICH host element: https://docs.strich.io/getting-started.html#defining-the-host-element
        Div hostElem = new Div();
        hostElem.setId("strich-container");
        hostElem.setWidthFull();
        hostElem.setHeight("240px");
        hostElem.getStyle().setPosition(Style.Position.RELATIVE);
        hostElem.getStyle().setBackgroundColor("#000");
        add(hostElem);

        // label for displaying barcodes or error message
        barcodes = new ArrayList<>();
        barcodeLabels = new Div[NUM_BARCODES];
        for (int i = 0; i < barcodeLabels.length; i++) {
            barcodeLabels[i] = new Div("Code " + (i + 1) + ": -");
            add(barcodeLabels[i]);
        }

        Button finishButton = new Button("FINISH SCANNING");
        finishButton.addClickListener(e -> {

            // tear down BarcodeReader and navigate back
            UI.getCurrent().getPage().executeJs("""
                if (window.barcodeReader) {
                    window.barcodeReader.destroy();
                    window.barcodeReader = undefined;
                    console.debug('BarcodeReader destroyed');
                }""").then((v) -> {
                // it would be preferable to tie destruction to Component Detach, but we can't synchronously invoke
                // JS from there... TODO: better solutions?
                UI.getCurrent().navigate("/");
            });
        });
        add(finishButton);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {

        // install callback handlers, allows JS to pass back something to this component
        UI.getCurrent().getPage().executeJs("""
                window.onBarcodeScanned = (data) => {
                    $0.$server.onBarcodeScanned(data);
                };
                window.onInitializationError = (msg) => {
                    $0.$server.onInitializationError(msg);
                };
                """, this);

        // initialize STRICH SDK, requires handlers to be present (may receive error during init)
        UI.getCurrent().getPage().addJavaScript("scanner-init.js", LoadMode.EAGER);
        UI.getCurrent().getPage().executeJs("window.initializeSDKAndBarcodeReader()");
    }

    @ClientCallable
    private void onInitializationError(String message) {
        Notification.show("ERR: " + message);
    }

    @ClientCallable
    private void onBarcodeScanned(String data) {
        if (barcodes.size() == NUM_BARCODES) {
            return;
        }
        barcodes.add(data);
        int number = barcodes.size();
        barcodeLabels[number-1].setText("Code " + number + ": " + data);
    }
}
