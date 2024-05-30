window.initializeSDKAndBarcodeReader = async () => {

    // initialize SDK with license key obtained through Customer Portal (must include app URL in scope)
    if (!strich.StrichSDK.isInitialized()) {
        const licenseKey = '<your license key>';
        try {
            await strich.StrichSDK.initialize(licenseKey);
        } catch (e) {
            if (typeof(window.onInitializationError) === 'function') {
                window.onInitializationError(e.message);
            } else {
                console.error(e.message);
            }
            return;
        }
    }
    console.debug(`STRICH SDK initialized`);

    // BarcodeReader configuration, see: https://docs.strich.io/reference/interfaces/Configuration.html
    const cfg = {
        selector: '#strich-container',
        frameSource: {
            resolution: 'full-hd'
        },
        engine: {
            symbologies: ['code128', 'qr']
        },
        feedback: {
            audio: true,
            vibration: false
        }
    }

    // initialize and start BarcodeReader: will obtain camera and start scanning barcodes
    try {
        const barcodeReader = new strich.BarcodeReader(cfg);
        barcodeReader.detected = (detections) => {
            if (typeof(window.onBarcodeScanned) === 'function') {
                window.onBarcodeScanned(detections[0].data);
            }
        };
        await barcodeReader.initialize();
        await barcodeReader.start();
        window.barcodeReader = barcodeReader;
        console.debug(`BarcodeReader initialized and started`);
    } catch (e) {
        if (typeof(window.onInitializationError) === 'function') {
            window.onInitializationError(e.message);
        } else {
            console.error(e.message);
        }
        return;
    }
}
console.debug(`Installed initializeSDKAndBarcodeReader function in global scope`);
