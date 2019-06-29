package game.main;

import davecode.log.Logger;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.ugens.Sampler;
import processing.core.PImage;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ResourceManager {
  private static Minim minim;

  private static HashMap<String, Resource> registry = new HashMap<>();

  public static void setup() {
    minim = new Minim(Window.instance);
  }

  enum ResourceType {
    IMAGE,
    SOUND,
    MUSIC,
  }

  private static class Resource {
    ResourceType type;
    PImage image = null;
    Sampler sound = null;
    AudioPlayer music = null;

    public Resource(PImage image) {
      this.type = ResourceType.IMAGE;
      this.image = image;
    }
    public Resource(Sampler sound) {
      this.type = ResourceType.SOUND;
      this.sound = sound;
    }
    public Resource(AudioPlayer music) {
      this.type = ResourceType.MUSIC;
      this.music = music;
    }
  }

  public static PImage getImage(String id) {
    if(registry.containsKey(id)) {
      Resource resource = registry.get(id);
      if(resource.type == ResourceType.IMAGE) {
        return resource.image;
      } else {
        Logger.error("Cannot Get Image " + id + " as it is not a image file.");
      }
    } else {
      Logger.error("Cannot Get Image " + id + " as it is not loaded, use loadImage(\"" + id + "\") to load it.");
    }
    return null;
  }

  public static void loadImage(String id) {
    // return if already added.
    if (registry.containsKey(id)) {
      return;
    }

    PImage image = Window.instance.loadImage("res/img/" + id + ".png");
    registry.put(id, new Resource(image));
  }

  public static void loadSound(String id) {
    // return if already added.
    if (registry.containsKey(id)) {
      return;
    }

    Sampler sampler = new Sampler("res/sfx/" + id + ".wav", 12, minim);
    sampler.patch(minim.getLineOut());

    registry.put(id, new Resource(sampler));
  }
  public static void loadMusic(String id) {
    // return if already added.
    if (registry.containsKey(id)) {
      return;
    }

    AudioPlayer sampler = minim.loadFile("res/sfx/" + id + ".wav");

    registry.put(id, new Resource(sampler));
  }

  public static void playSound(String id) {
    if(registry.containsKey(id)) {
      Resource resource = registry.get(id);
      if(resource.type == ResourceType.SOUND) {
        resource.sound.trigger();
      } else {
        Logger.error("Cannot Play Sound " + id + " as it is not a sound file.");
      }
    } else {
      Logger.error("Cannot Play Sound " + id + " as it is not loaded, use loadSound(\"" + id + "\") to load it.");
    }
  }

  public static void playMusic(String id) {
    if(registry.containsKey(id)) {
      Resource resource = registry.get(id);
      if(resource.type == ResourceType.MUSIC) {
        resource.music.loop();
        resource.music.setGain(7.5f);
      } else {
        Logger.error("Cannot Play Music " + id + " as it is not a music file.");
      }
    } else {
      Logger.error("Cannot Play Music " + id + " as it is not loaded, use loadSound(\"" + id + "\") to load it.");
    }
  }

  public static void stopAllSounds() {
    for (Resource resource: registry.values()) {
      if(resource.sound != null) {
        resource.sound.stop();
      }
      if(resource.music != null) {
        resource.music.pause();
        resource.music.rewind();
      }
    }
  }
  public static void stopMusic() {
    for (Resource resource: registry.values()) {
      if(resource.music != null) {
        resource.music.pause();
        resource.music.rewind();
      }
    }
  }
}

