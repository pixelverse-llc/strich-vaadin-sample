package io.strich.samples.vaadin.views.scanning;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.ui.LoadMode;

@PageTitle("Repeated Scans")
@Route("scan-repeated")
@JavaScript(value = "https://cdn.jsdelivr.net/npm/@pixelverse/strichjs-sdk@1.5.4/dist/strich-noesm.js", loadMode = LoadMode.EAGER)
public class ScanRepeatedView extends VerticalLayout {

    private int codesScanned = 0;

    public ScanRepeatedView() {
        setHeightFull();

        add(new H2("Repeated Scans"));

        // STRICH host element: https://docs.strich.io/getting-started.html#defining-the-host-element
        Div hostElem = new Div();
        hostElem.setId("strich-container");
        hostElem.setWidthFull();
        hostElem.setHeightFull();
        hostElem.getStyle().setPosition(Style.Position.RELATIVE);
        hostElem.getStyle().setBackgroundColor("#000");
        add(hostElem);

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
                    window.barcodeReader.stop().then(() => {
                        $0.$server.onBarcodeScanned(data);
                    });
                };
                window.onInitializationError = (msg) => {
                    $0.$server.onInitializationError(msg);
                };
                """, this);

        // initialize STRICH SDK, requires handlers to be present (may receive error during init)
        UI.getCurrent().getPage().addJavaScript("scanner-init.js", LoadMode.EAGER);
        UI.getCurrent().getPage().executeJs("window.initializeSDKAndBarcodeReader()");
    }

    private void continueScanning() {
        UI.getCurrent().getPage().executeJs("""
                window.barcodeReader.start();
                """);
    }

    private void finishScanning() {
        UI.getCurrent().getPage().executeJs("""
                window.barcodeReader.destroy();
                """).then(v -> UI.getCurrent().navigate("/"));
    }

    @ClientCallable
    private void onInitializationError(String message) {
        Notification.show("ERR: " + message);
    }

    @ClientCallable
    private void onBarcodeScanned(String data) {
        codesScanned++;

        final Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.setModal(true);
        dialog.add("You have scanned " + codesScanned + " barcodes.");
        dialog.add("Scanned code: " + data);
        Button continueButton = new Button("CONTINUE", (e) -> {
            dialog.close();
            continueScanning();
        });
        Button finishButton = new Button("FINISH", (e) -> {
            dialog.close();
            finishScanning();
        });
        dialog.getFooter().add(continueButton);
        dialog.getFooter().add(finishButton);
        dialog.open();
    }
}
