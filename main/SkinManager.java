package main;

import java.io.File;

/**
 * SkinManager: wrapper UI untuk mengatur path skin bidak tanpa mengubah logika / aturan game.
 *
 * Mekanisme: BidakMngr tetap memanggil imageLoader.load("asset/img/W_pawn.png") dsb.
 * Lalu di level loader, path akan dialihkan (redirect) ke folder skin yang dipilih.
 *
 * // UI ONLY CHANGE
 */
public final class SkinManager {

    private SkinManager() {}

    /**
     * Redirect path default (asset/img/...) ke folder skin aktif (asset/skins/<set>/...).
     * Jika file tidak ada, fallback ke path asli.
     */
    public static String resolvePiecePath(String originalPath) {
        if (originalPath == null) return null;

        // Hanya redirect untuk gambar bidak yang mengikuti format lama.
        // Contoh: asset/img/W_pawn.png
        final String prefix = "asset/img/";
        if (!originalPath.startsWith(prefix)) return originalPath;

        String fileName = originalPath.substring(prefix.length());
        String alt = UIConfig.getSelectedSkinDir() + "/" + fileName;

        // jika file skin tersedia, gunakan; kalau tidak, fallback.
        if (new File(alt).exists()) return alt;
        return originalPath;
    }
}
