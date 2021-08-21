package name.neuhalfen.cmssignaturevalidation;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CMSValidationTest {
    X509Certificate cert(String folder) throws IOException, CertificateException, NoSuchProviderException {
        try (InputStream certificate = App.getContext().getAssets().open(  folder + "/signer.crt")) {
            return Util.loadCertificate(certificate);
        }
    }

    QRCodeParser.QRCode code(String folder) throws IOException {

        try (InputStream resourceAsStream =  App.getContext().getAssets().open( folder + "/qrcode.spec")) {
            byte[] data = new byte[8192];
            int len = Objects.requireNonNull(resourceAsStream).read(data);
            return QRCodeParser.parse(new String(data, 0, len));

        }
    }

    QRCodeParser.QRCode untrustedQRCode() throws IOException {
        return code("untrusted");
    }

    QRCodeParser.QRCode trustedQRCode() throws IOException {
        return code("trusted");
    }

    X509Certificate trustedCert() throws IOException, CertificateException, NoSuchProviderException {
        return cert("trusted");
    }

    X509Certificate untrustedCert() throws IOException, CertificateException, NoSuchProviderException {
        return cert("untrusted");
    }

    public List<String> runTests() {
        final List<String> results = new ArrayList<>();
        results.add(notTamperedGoodCert_validates() );
        results.add(tamperedGoodCert_validatesNot() );
        results.add(notTamperedSignedWithWrongCert_validatesNot() );

        return results;
    }

    public String notTamperedGoodCert_validates() {
        CMSSignatureValidation sut = new CMSSignatureValidation();
        try {
            QRCodeParser.QRCode qrCode = trustedQRCode();
            X509Certificate trustedCert = trustedCert();
            return "notTamperedGoodCert_validates " + sut.validateSignature(trustedCert, qrCode.data, qrCode.signature);
        } catch (Exception e) {
            e.printStackTrace();
            return "notTamperedGoodCert_validates " + e.toString();
        }
    }

    public String tamperedGoodCert_validatesNot() {
        CMSSignatureValidation sut = new CMSSignatureValidation();
        try {
            QRCodeParser.QRCode qrCode = trustedQRCode();
            String tampered = qrCode.data.replaceFirst("PLAP", "TAMP");
            X509Certificate trustedCert = trustedCert();
            return "tamperedGoodCert_validatesNot " + !(sut.validateSignature(trustedCert, tampered, qrCode.signature));
        } catch (Exception e) {
            e.printStackTrace();
            return "tamperedGoodCert_validatesNot " + e.toString();
        }
    }

    /**
     * Signing and validation certs are different. This will fail.
     */
    public String notTamperedSignedWithWrongCert_validatesNot()  {
        CMSSignatureValidation sut = new CMSSignatureValidation();
        try {
            QRCodeParser.QRCode qrCode = untrustedQRCode();
            X509Certificate trustedCert = trustedCert();

            return "notTamperedSignedWithWrongCert_validatesNot " + !(sut.validateSignature(trustedCert, qrCode.data, qrCode.signature));
        } catch (Exception e) {
            e.printStackTrace();
            return "notTamperedSignedWithWrongCert_validatesNot " + e.toString();
        }
    }

}
