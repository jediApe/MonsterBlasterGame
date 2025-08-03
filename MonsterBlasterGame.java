package com.monsterblaster;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MonsterBlasterGame extends ApplicationAdapter {
    SpriteBatch batch;
    Texture mapTexture, bulletTexture, enemyTexture;
    Player player;
    List<Enemy> enemies;
    List<Bullet> bullets;
    float spawnTimer;
    int wave;
    int score;

    // ---------------- Character System ----------------
    abstract static class Character {
        String name;
        float health, maxHealth, speed, damage, fireRate;
        Vector2 position;
        Texture texture;

        Character(String name, float health, float speed, float damage, float fireRate) {
            this.name = name;
            this.maxHealth = health;
            this.health = health;
            this.speed = speed;
            this.damage = damage;
            this.fireRate = fireRate;
            this.position = new Vector2(400, 300);
            this.texture = new Texture("character_" + name.toLowerCase() + ".png");
        }

        abstract void specialAbility(List<Bullet> bullets);

        void move(float deltaX, float deltaY) {
            position.x = MathUtils.clamp(position.x + deltaX * speed, 0, 800 - 32);
            position.y = MathUtils.clamp(position.y + deltaY * speed, 0, 600 - 32);
        }

        void takeDamage(float damage) {
            health = Math.max(0, health - damage);
        }
    }

    static class Sniper extends Character {
        Sniper() {
            super("Sniper", 50, 2f, 20, 0.5f);
        }

        void specialAbility(List<Bullet> bullets) {
            bullets.add(new Bullet(position.x, position.y, damage * 2, 300));
        }
    }

    static class Tank extends Character {
        Tank() {
            super("Tank", 100, 1f, 15, 0.8f);
        }

        void specialAbility(List<Bullet> bullets) {
            health = Math.min(health + 20, maxHealth);
        }
    }

    static class Mage extends Character {
        Mage() {
            super("Mage", 70, 1.5f, 10, 0.3f);
        }

        void specialAbility(List<Bullet> bullets) {
            bullets.add(new Bullet(position.x, position.y, damage * 1.5f, 150, true));
        }
    }

    static class Rogue extends Character {
        Rogue() {
            super("Rogue", 60, 3f, 8, 1.2f);
        }

        void specialAbility(List<Bullet> bullets) {
            fireRate *= 2;
        }
    }

    // ---------------- Player ----------------
    static class Player {
        Character character;
        float fireCooldown;
        int level;
        int points;

        Player(Character character) {
            this.character = character;
            this.fireCooldown = 0;
            this.level = 1;
            this.points = 0;
        }

        void shoot(float targetX, float targetY, List<Bullet> bullets) {
            if (fireCooldown <= 0) {
                Vector2 direction = new Vector2(targetX - character.position.x, targetY - character.position.y).nor();
                bullets.add(new Bullet(character.position.x, character.position.y, character.damage, 300, false, direction));
                fireCooldown = 1f / character.fireRate;
            }
        }

        void upgrade(String attribute) {
            if (points <= 0) return;
            points--;
            switch (attribute) {
                case "damage" -> character.damage += 5;
                case "fireRate" -> character.fireRate += 0.2f;
                case "health" -> character.maxHealth += 10;
            }
        }
    }

    // ---------------- Enemy ----------------
    static class Enemy {
        float health, speed;
        Vector2 position;

        Enemy(float x, float y) {
            this.health = 20;
            this.speed = 1;
            this.position = new Vector2(x, y);
        }

        void update(Player player, float delta) {
            Vector2 direction = new Vector2(player.character.position).sub(position).nor();
            position.mulAdd(direction, speed * delta * 100);
        }
    }

    // ---------------- Bullet ----------------
    static class Bullet {
        Vector2 position, direction;
        float damage, speed;
        boolean isAoE;

        Bullet(float x, float y, float damage, float speed) {
            this(x, y, damage, speed, false, new Vector2(1, 0)); // default right
        }

        Bullet(float x, float y, float damage, float speed, boolean isAoE) {
            this(x, y, damage, speed, isAoE, new Vector2(1, 0)); // default right
        }

        Bullet(float x, float y, float damage, float speed, boolean isAoE, Vector2 direction) {
            this.position = new Vector2(x, y);
            this.damage = damage;
            this.speed = speed;
            this.isAoE = isAoE;
            this.direction = direction.nor();
        }

        void update(float delta) {
            position.mulAdd(direction, speed * delta);
        }
    }

    // ---------------- Game Lifecycle ----------------
    @Override
    public void create() {
        batch = new SpriteBatch();
        mapTexture = new Texture("forest_map.png");
        bulletTexture = new Texture("bullet.png");
        enemyTexture = new Texture("enemy.png");
        player = new Player(new Sniper());

        enemies = new ArrayList<>();
        bullets = new ArrayList<>();
        spawnTimer = 3f;
        wave = 1;
        score = 0;

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDragged(int x, int y, int pointer) {
                float deltaX = (x - player.character.position.x) / 100;
                float deltaY = ((Gdx.graphics.getHeight() - y) - player.character.position.y) / 100;
                player.character.move(deltaX, deltaY);
                return true;
            }

            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                player.shoot(x, Gdx.graphics.getHeight() - y, bullets);
                return true;
            }
        });
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update logic
        player.fireCooldown -= delta;
        spawnTimer -= delta;

        if (spawnTimer <= 0) {
            spawnEnemies();
            spawnTimer = 3f;
        }

        // Update enemies
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.update(player, delta);
            if (enemy.position.dst(player.character.position) < 20) {
                player.character.takeDamage(10);
                enemyIterator.remove();
            }
        }

        // Update bullets
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update(delta);
            boolean hit = false;

            Iterator<Enemy> eIt = enemies.iterator();
            while (eIt.hasNext()) {
                Enemy enemy = eIt.next();
                if (bullet.position.dst(enemy.position) < 15) {
                    enemy.health -= bullet.damage;
                    if (enemy.health <= 0) {
                        eIt.remove();
                        score += 10;
                        player.points += 1;
                    }
                    if (!bullet.isAoE) hit = true;
                }
            }

            if (bullet.position.x > 800 || bullet.position.x < 0 || hit) {
                bulletIterator.remove();
            }
        }

        // Leveling up
        if (score >= wave * 100) {
            wave++;
            player.level++;
            for (Enemy enemy : enemies) {
                enemy.health += 5;
                enemy.speed += 0.1f;
            }
        }

        // Drawing
        batch.begin();
        batch.draw(mapTexture, 0, 0);
        batch.draw(player.character.texture, player.character.position.x, player.character.position.y);
        for (Enemy enemy : enemies) {
            batch.draw(enemyTexture, enemy.position.x, enemy.position.y);
        }
        for (Bullet bullet : bullets) {
            batch.draw(bulletTexture, bullet.position.x, bullet.position.y);
        }
        batch.end();
    }

    void spawnEnemies() {
        for (int i = 0; i < wave * 2; i++) {
            float x = MathUtils.random(0, 800);
            float y = MathUtils.random(0, 600);
            enemies.add(new Enemy(x, y));
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        mapTexture.dispose();
        bulletTexture.dispose();
        enemyTexture.dispose();
        player.character.texture.dispose();
    }
}
