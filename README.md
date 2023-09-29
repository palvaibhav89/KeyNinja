# KeyNinja - Android Studio Plugin for Securing API Keys
KeyNinja is an Android Studio plugin that provides a secure and efficient way to manage API keys within your Android application. By using KeyNinja, you can avoid storing sensitive keys directly in the codebase, reducing the risk of exposure through reverse engineering.

## How it Works
When integrating KeyNinja into your Android project, you need to create a **'keys.json'** file that contains all the API keys for different flavors and variants of your application. KeyNinja will read this **'keys.json'** file and store the keys securely in the MacOS Keychain. It then generates dynamic Java static files that hold the keys, based on the specified flavors and variants.

## Steps to Integrate
Follow these steps to integrate KeyNinja into your Android project:

1. **Build keys.json**: Create a keys.json file containing the API keys for different environments (development, production, etc.) and flavors. You can specify keys as constants or string resources depending on your needs. The default section can be used for keys that are common across all flavors.

2. **Fork the Project and Build the Jar File**: Fork the KeyNinja project and build a JAR file. Then, add the JAR file to your project and include the following lines in your **project-level build.gradle** file:

```
repositories {
    flatDir name: 'libs', dirs: "path_to_jar"
}

dependencies {
    classpath 'keyninja:keyninja:1.0'
}
```
3. **Add KeyNinja Plugin to App-level build.gradle**: Apply the KeyNinja plugin in your **app-level build.gradle** file using the following line:

```
plugins {
    id 'com.techknights.keyninja'
}
```
4. **Configure KeyNinja**: In your app-level build.gradle file, add the following configuration for KeyNinja:

```
keyNinja {
    keyJsonVersion = 2 // Increment this whenever you change keys.json
    keyJsonPath = rootDir.path + "/keys.json" // Path to keys.json file
}
```

## Usage
**Access API Keys**: After integrating KeyNinja, you can access the API keys in your code using the generated Java static file, e.g., PKeys.fb_app_provider.

**String resources** will be automatically generated and can be used as R.string.xxx

**keys.json Example**

```
{
  "development": {
    "fb_app_id": {
      "value": "your_fb_app_id_here",
      "type": "constant"
    },
    "fb_app_provider": {
      "value": "your_fb_app_provider_here",
      "type": "string"
    }
  },
  "production": {
    "fb_app_id": {
      "value": "your_fb_app_id_here",
      "type": "constant"
    },
    "fb_app_provider": {
      "value": "your_fb_app_provider_here",
      "type": "string"
    }
  },
  "default": {
    "fb_app_id_xxx": {
      "value": "your_common_fb_app_id_here",
      "type": "constant"
    }
  }
}
```

## Notes
1. Make sure to increment keyJsonVersion in the keyNinja configuration whenever you make changes to keys.json.
2. The default section is used for keys that are common across all flavors.
