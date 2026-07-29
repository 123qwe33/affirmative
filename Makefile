.PHONY: setup build deploy clean

# Run once to generate the Gradle wrapper (requires gradle on PATH)
setup:
	gradle wrapper --gradle-version=8.7
	chmod +x gradlew

build:
	./gradlew assembleDebug

deploy: build
	adb install -r app/build/outputs/apk/debug/app-debug.apk

clean:
	./gradlew clean
