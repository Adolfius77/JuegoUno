# Poner el servidor en la nube

Para que alguien juegue contigo desde su casa, el servidor tiene que estar en
una máquina a la que lleguéis los dos. Esta guía deja el servidor corriendo en
una máquina alquilada y a los dos jugadores conectándose a ella.

Tiempo: un rato la primera vez; después es encender y jugar.

---

## Antes de empezar: lo que hay que saber

**El juego no pide contraseña ni cifra nada.** Con el servidor en una IP
pública, cualquiera que dé con el puerto puede registrarse y entrar a las salas.
Para echar partidas entre amigos es asumible, pero conviene saberlo y no dejarlo
encendido meses sin mirarlo.

La forma barata de acotarlo, si tu proveedor lo permite, es **limitar el
cortafuegos a vuestras IP** en vez de abrir el puerto al mundo (más abajo se
explica). Poner usuarios y contraseñas de verdad sería otro trabajo, y hoy no
está hecho.

---

## 1. Construir el servidor

En tu PC, en la carpeta del proyecto:

```bash
mvn clean install
```

Eso deja dos archivos, cada uno con todo dentro:

| Archivo | Qué es |
|---|---|
| `SERVER-PROXY/target/servidor.jar` | El servidor. Es el que sube a la nube. |
| `Launcher/target/JuegoUno.jar` | El juego. Es el que usáis tú y tu amigo. |

## 2. Alquilar la máquina

Sirve cualquier VPS con Linux. Opciones:

- **Oracle Cloud**, capa gratuita: da una máquina pequeña sin coste, de sobra
  para esto.
- **Hetzner**, **DigitalOcean**, **Vultr**: entre 4 y 5 € al mes.

Elige Ubuntu. Apunta la **IP pública** que te dé el panel: es la que le vas a
pasar a tu amigo.

## 3. Preparar la máquina

Conéctate por SSH y instala Java 21:

```bash
ssh ubuntu@TU-IP-PUBLICA

sudo apt update
sudo apt install -y openjdk-21-jre-headless
java -version          # tiene que decir 21
```

## 4. Subir el servidor

Desde **tu PC**, en la carpeta del proyecto:

```bash
scp SERVER-PROXY/target/servidor.jar ubuntu@TU-IP-PUBLICA:~/
```

Y en la máquina, para probar que arranca:

```bash
java -jar servidor.jar
```

Tiene que decir algo así:

```
[Servidor Red] Escuchando en el puerto 8080 (todas las interfaces).
[Servidor Red] En esta misma maquina: localhost:8080
[Servidor Red] En la red local:     10.0.0.5:8080
[Servidor Red] Si esto corre en la nube, reparte la IP publica que da el panel
               de tu proveedor: desde dentro no se ve.
```

Esa `10.0.0.5` es la IP interna de la máquina, **no** la que hay que repartir.
La buena es la pública del panel.

Déjalo corriendo de momento y pasa al paso siguiente en otra terminal.

## 5. Abrir el puerto — son DOS sitios

Este es el paso donde casi todo el mundo se atasca, porque hay **dos**
cortafuegos y hay que abrir el puerto en los dos.

**a) El de la propia máquina:**

```bash
sudo ufw allow 8080/tcp
sudo ufw status
```

**b) El del proveedor**, en su panel web. Busca "Security List", "Firewall
Rules", "Networking" o "Security Groups" según quién sea, y añade una regla de
entrada: **TCP, puerto 8080**.

Si quieres acotarlo a vosotros dos en vez de abrirlo a todo internet, en esa
regla pon vuestras IP públicas en lugar de `0.0.0.0/0`. Las ves entrando a
cualquier web del estilo "cuál es mi IP". El pero: muchas conexiones domésticas
cambian de IP cada cierto tiempo, y entonces hay que actualizar la regla.

**Comprueba que se llega desde fuera**, desde tu PC:

```bash
# Windows (PowerShell)
Test-NetConnection TU-IP-PUBLICA -Port 8080

# Linux o Mac
nc -vz TU-IP-PUBLICA 8080
```

Si esto no responde, no sigas: el problema está en el cortafuegos, no en el
juego.

## 6. Que no se apague al cerrar la sesión

Tal cual, el servidor muere al cerrar el SSH. Con `systemd` arranca solo y
sobrevive a los reinicios.

Crea el archivo:

```bash
sudo nano /etc/systemd/system/juegouno.service
```

Con esto dentro (cambia `ubuntu` si tu usuario es otro):

```ini
[Unit]
Description=Servidor de JuegoUno
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/home/ubuntu
ExecStart=/usr/bin/java -jar /home/ubuntu/servidor.jar
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
```

Y lo enciendes:

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now juegouno
sudo systemctl status juegouno      # tiene que decir active (running)
```

Para ver lo que va pasando en las partidas:

```bash
journalctl -u juegouno -f
```

Para actualizarlo cuando cambies el código: subes el jar nuevo con `scp` y
`sudo systemctl restart juegouno`.

## 7. Jugar

Tu amigo solo necesita **dos cosas**: el archivo `JuegoUno.jar` y tu IP pública.

1. Le pasas `Launcher/target/JuegoUno.jar` (por WeTransfer, Drive, lo que sea).
   Necesita tener Java 21 instalado.
2. Lo abre con doble clic, o desde la terminal:

   ```bash
   java -jar JuegoUno.jar
   ```

3. Al abrirse sale una ventanita pidiendo la dirección del servidor. Escribe tu
   IP pública y le da a OK. **Se queda recordada**, así que la próxima vez solo
   tiene que darle a OK.

   Si prefiere no escribir nada, también vale pasarla al arrancar:

   ```bash
   java -jar JuegoUno.jar TU-IP-PUBLICA
   ```

4. Uno de los dos crea la sala, le pasa el código de cuatro letras al otro, y el
   otro se une con ese código.

Si quieres meter un bot a la partida, apuntándolo al mismo servidor:

```bash
java -Dservidor.ip=TU-IP-PUBLICA -cp JuegoUno.jar Launcher.BotMain
```

---

## Cuando algo no va

| Qué pasa | Qué mirar |
|---|---|
| "No se pudo conectar con..." | Que el servidor esté encendido (`systemctl status juegouno`) y que el puerto esté abierto en **los dos** cortafuegos. |
| El `nc`/`Test-NetConnection` no responde | Es el cortafuegos, casi siempre el del panel del proveedor. |
| Conecta pero se corta a los pocos minutos | Mira `journalctl -u juegouno -f` mientras juegas. |
| El otro no ve la sala | Que los dos estéis apuntando al **mismo** servidor: se ve en la consola, `[Cliente-Proxy] Conectando a ...`. |
| Aparecen jugadores que ya no están | Ya está arreglado, pero hay que tener el servidor actualizado: sube el jar nuevo y reinicia el servicio. |

## Alternativas, si no quieres alquilar nada

- **Misma casa / misma wifi**: no hace falta nada de esto. Arranca el servidor
  en tu PC y que el otro escriba tu IP local, la que sale como "En la red local"
  al arrancarlo.
- **Tailscale o ZeroTier**: os instaláis los dos el programa y os da una IP
  privada a cada uno, como si estuvierais en la misma casa. Sin tocar routers y
  sin quedar expuesto a internet. Es la opción más segura si no quieres pagar.
