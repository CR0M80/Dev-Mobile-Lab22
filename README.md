# 📋 JNIDemo — By SAAD

Application Android démontrant la communication entre Java et C++ via JNI
avec saisie utilisateur pour chaque opération native.

---

## 📽️ Démonstration



https://github.com/user-attachments/assets/8cd9af9c-85de-4d31-962f-c368750c1f26


---

# 🏗️ Architecture
MainActivity (Java) → saisie utilisateur → méthode native → C++ → résultat → UI

| Couche    | Rôle                                    |
| --------- | --------------------------------------- |
| Java      | Collecte les inputs et appelle le natif |
| JNI       | Passerelle de communication             |
| C++ / NDK | Exécute les calculs                     |
| CMake     | Compile `native-lib.cpp` en `.so`       |

---

# 🖼️ Interface — `activity_main.xml`

| Élément         | Rôle                                         |
| --------------- | -------------------------------------------- |
| `btnHello`      | Déclenche `helloFromJNI()`                   |
| `etFactInput`   | Saisie de l'entier pour le factoriel         |
| `btnFact`       | Envoie la valeur à `factorial(n)`            |
| `etReverseInput`| Saisie du texte à inverser                   |
| `btnReverse`    | Envoie le texte à `reverseString(s)`         |
| `etArrayInput`  | Saisie des entiers séparés par des virgules  |
| `btnArray`      | Envoie le tableau à `sumArray(int[])`        |

---

# 🔧 Code natif — `native-lib.cpp`

## 1. Hello World natif

```cpp
return env->NewStringUTF("Bonjour depuis le C++ natif via JNI !");
```

## 2. Factoriel avec contrôle d'erreur

| Code retour | Signification         |
| ----------- | --------------------- |
| `>= 0`      | Résultat valide       |
| `-1`        | Entrée négative       |
| `-2`        | Dépassement `INT_MAX` |

## 3. Inversion de chaîne

```cpp
env->GetStringUTFChars → std::reverse → env->ReleaseStringUTFChars
```

## 4. Somme d'un tableau

```cpp
env->GetIntArrayElements → boucle → env->ReleaseIntArrayElements(JNI_ABORT)
```

---

# ☕ Logique — `MainActivity.java`

## Parsing du tableau saisi par l'utilisateur

```java
String[] parties = saisie.split(",");
int[] nombres = new int[parties.length];
for (int i = 0; i < parties.length; i++) {
    nombres[i] = Integer.parseInt(parties[i].trim());
}
```

👉 Toutes les valeurs viennent de l'utilisateur, aucune valeur codée en dur.

---

# 🔍 Logs — Logcat (tag : `JNI_DEMO`)
helloFromJNI : execution dans la couche native
factorial(7) = 5040
reverseString : resultat = dlroW olleH
sumArray : somme de 4 elements = 100

---

# 📚 Concepts clés

| Concept                 | Explication                             |
| ----------------------- | --------------------------------------- |
| `GetStringUTFChars`     | Convertit `String` Java en `char*` C    |
| `GetIntArrayElements`   | Accède au contenu d'un `int[]` Java     |
| `JNI_ABORT`             | Libère sans réécrire le tableau Java    |
| `System.loadLibrary()`  | Charge `libnative-lib.so` au démarrage  |
| `split(",")`            | Parse la saisie en tableau d'entiers    |

---

*Projet réalisé dans le cadre d'un apprentissage Android — SAAD* 🚀

README Lab 2
markdown# 📋 JNIDemo — Anti-Debug — By SAAD

Extension du lab JNI de base avec couche défensive native.
Les fonctions natives sont déverrouillées uniquement après vérification
de l'environnement d'exécution par l'utilisateur.

---

## 📽️ Démonstration

<!-- Ajouter votre vidéo ici -->

---

# 🏗️ Architecture
Bouton "Vérifier" → isDebugDetected() → C++
→ isBeingTraced()
→ containsSuspiciousLibraryNames()
→ jboolean → UI active ou bloque les fonctions

---

# 🖼️ Interface — `activity_main.xml`

| Élément       | Rôle                                              |
| ------------- | ------------------------------------------------- |
| `btnCheck`    | Lance la vérification de sécurité native          |
| `tvStatus`    | Affiche l'état (OK en vert / suspect en rouge)    |
| `btnHello`    | Appelle `helloFromJNI()` si environnement sûr     |
| `etFactInput` | Saisie de l'entier pour le factoriel              |
| `btnFact`     | Envoie la valeur à `factorial(n)` si sûr          |

👉 Les boutons et champs sont désactivés par défaut, activés uniquement si `isDebugDetected()` retourne `false`.

---

# 🔧 Code natif — `native-lib.cpp`

## `isBeingTraced()`

```cpp
long resultat = ptrace(PTRACE_TRACEME, 0, 0, 0);
if (resultat == -1) return true;
```

## `containsSuspiciousLibraryNames()`

```cpp
FILE* maps = fopen("/proc/self/maps", "r");
while (fgets(ligne, sizeof(ligne), maps)) {
    if (strstr(ligne, "frida") || strstr(ligne, "xposed") ...) return true;
}
```

| Signature    | Outil associé               |
| ------------ | --------------------------- |
| `frida`      | Framework d'instrumentation |
| `xposed`     | Framework de hook Java      |
| `gdbserver`  | Débogueur GDB               |
| `magisk`     | Outil de root               |

## `isDebugDetected()`

```cpp
if (traced || suspiciousMaps) return JNI_TRUE;
return JNI_FALSE;
```

---

# ☕ Logique — `MainActivity.java`

## Déverrouillage conditionnel

```java
btnCheck.setOnClickListener(v -> {
    boolean suspect = isDebugDetected();
    if (suspect) {
        // UI bloquee
    } else {
        environnementSur = true;
        // UI activee
    }
});
```

## Protection des actions

```java
if (!environnementSur) {
    Toast.makeText(this, "Environnement non verifie", ...).show();
    return;
}
```

👉 Même si les boutons sont activés, un second contrôle logique est présent.

---

# 🔍 Logs — Logcat (tag : `ANTI_DEBUG`)
isBeingTraced : aucun traceur detecte
containsSuspiciousLibraryNames : aucune signature suspecte
isDebugDetected : environnement OK

Ou en contexte suspect :
isBeingTraced : trace active detectee
isDebugDetected : environnement suspect confirme

---

# ⚠️ Limites

| Limite               | Explication                                        |
| -------------------- | -------------------------------------------------- |
| Détection imparfaite | Aucun contrôle simple ne couvre tous les cas       |
| Faux positifs        | Certains environnements de dev peuvent déclencher  |
| Contournement        | Un attaquant expérimenté peut neutraliser ces contrôles |

---

# 📚 Concepts clés

| Concept                   | Explication                                   |
| ------------------------- | --------------------------------------------- |
| `ptrace(PTRACE_TRACEME)`  | Détecte si le processus est déjà tracé        |
| `/proc/self/maps`         | Cartographie mémoire du processus             |
| `JNI_TRUE / JNI_FALSE`    | Booléens retournés vers Java                  |
| `environnementSur`        | Flag Java contrôlant l'accès aux fonctions    |
| Désactivation UI          | `setEnabled(false)` tant que non vérifié      |

---

*Projet réalisé dans le cadre d'un apprentissage Android — SAAD* 🚀
