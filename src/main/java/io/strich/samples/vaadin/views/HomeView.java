package io.strich.samples.vaadin.views;

import io.strich.samples.vaadin.views.scanning.ScanMultipleView;
import io.strich.samples.vaadin.views.scanning.ScanRepeatedView;
import io.strich.samples.vaadin.views.scanning.ScanSingleView;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLink;

@PageTitle("Home")
@Route("")
@RouteAlias(value = "")
public class HomeView extends VerticalLayout {

    public HomeView() {
        add(new H2("STRICH Vaadin Sample"));
        add(new Html("<p>Note: only <b>Code 128</b> and <b>QR</b> symbologies are enabled.</p>"));

        Section single = new Section();
        single.add(new H3("Single Scan"));
        single.add(new Paragraph("Scan a single barcode and return here."));
        single.add(new RouterLink("Start Single", ScanSingleView.class));
        add(single);

        Section repeated = new Section();
        repeated.add(new H3("Repeated Scans"));
        repeated.add(new Paragraph("Repeatedly scan barcodes, pausing and resuming the scanner until done."));
        repeated.add(new RouterLink("Start Repeated", ScanRepeatedView.class));
        add(repeated);

        Section multiple = new Section();
        multiple.add(new H3("Multiple Scans"));
        multiple.add(new Paragraph("Scan continuously until a number of distinct barcodes are scanned."));
        multiple.add(new RouterLink("Start Multiple", ScanMultipleView.class));
        add(multiple);
    }

}