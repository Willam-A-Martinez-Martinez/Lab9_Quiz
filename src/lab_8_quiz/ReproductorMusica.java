package lab_8_quiz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class ReproductorMusica extends JFrame {
    private ListaEnlazada listaReproduccion;
    private JList<Cancion> listaCanciones;
    private DefaultListModel<Cancion> modeloLista;
    private JButton btnPlay, btnPause, btnStop, btnAdd, btnRemove;
    private JLabel lblImagen, lblNombre, lblArtista, lblDuracion, lblGenero;
    private JPanel panelInfo;
    private MP3Player mp3Player;
    private int cancionActualIndice = -1;
    private boolean isPaused = false;
    private long pauseLocation = 0;
    private File currentFile = null;
    private final String ARCHIVO_PLAYLIST = "playlist.dat";
    
    public ReproductorMusica() {
        super("Reproductor de Música");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        listaReproduccion = new ListaEnlazada();
        initComponents();
        cargarPlaylist();
        setVisible(true);
    }
    
    private void initComponents() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel panelControl = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPlay = new JButton("Play");
        btnPause = new JButton("Pause");
        btnStop = new JButton("Stop");
        btnAdd = new JButton("Agregar");
        btnRemove = new JButton("Eliminar");
        panelControl.add(btnPlay);
        panelControl.add(btnPause);
        panelControl.add(btnStop);
        panelControl.add(btnAdd);
        panelControl.add(btnRemove);
        panelInfo = new JPanel(new BorderLayout(10, 10));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información de la Canción"));
        JPanel panelDatosCancion = new JPanel(new GridLayout(4, 1, 5, 5));
        lblNombre = new JLabel("Nombre: ");
        lblArtista = new JLabel("Artista: ");
        lblDuracion = new JLabel("Duración: ");
        lblGenero = new JLabel("Género: ");
        panelDatosCancion.add(lblNombre);
        panelDatosCancion.add(lblArtista);
        panelDatosCancion.add(lblDuracion);
        panelDatosCancion.add(lblGenero);
        lblImagen = new JLabel();
        lblImagen.setPreferredSize(new Dimension(150, 150));
        lblImagen.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lblImagen.setHorizontalAlignment(JLabel.CENTER);
        panelInfo.add(lblImagen, BorderLayout.WEST);
        panelInfo.add(panelDatosCancion, BorderLayout.CENTER);
        modeloLista = new DefaultListModel<>();
        listaCanciones = new JList<>(modeloLista);
        listaCanciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCanciones.setBorder(BorderFactory.createTitledBorder("Lista de Reproducción"));
        JScrollPane scrollLista = new JScrollPane(listaCanciones);
        panelPrincipal.add(panelControl, BorderLayout.NORTH);
        panelPrincipal.add(panelInfo, BorderLayout.CENTER);
        panelPrincipal.add(scrollLista, BorderLayout.SOUTH);
        add(panelPrincipal);
        btnAdd.addActionListener(e -> agregarCancion());
        btnRemove.addActionListener(e -> eliminarCancion());
        btnPlay.addActionListener(e -> reproducir());
        btnPause.addActionListener(e -> togglePausar());
        btnStop.addActionListener(e -> detener());
        listaCanciones.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = listaCanciones.getSelectedIndex();
                if (selectedIndex >= 0) {
                    mostrarInfoCancion(selectedIndex);
                }
            }
        });
        btnPlay.setEnabled(false);
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);
        btnRemove.setEnabled(false);
    }
    
    private void actualizarListaCanciones() {
        modeloLista.clear();
        Cancion[] canciones = listaReproduccion.obtenerTodasLasCanciones();
        for (Cancion cancion : canciones) {
            modeloLista.addElement(cancion);
        }
        boolean hayElementos = modeloLista.getSize() > 0;
        btnPlay.setEnabled(hayElementos);
        btnRemove.setEnabled(hayElementos);
    }
    
    private void mostrarInfoCancion(int indice) {
        if (indice >= 0 && indice < listaReproduccion.getTamaño()) {
            Cancion cancion = listaReproduccion.obtenerCancion(indice);
            lblNombre.setText("Nombre: " + cancion.getNombre());
            lblArtista.setText("Artista: " + cancion.getArtista());
            lblDuracion.setText("Duración: " + cancion.getDuracion());
            lblGenero.setText("Género: " + cancion.getGenero());
            String rutaImagen = cancion.getRutaImagen();
            if (rutaImagen != null && !rutaImagen.isEmpty()) {
                try {
                    ImageIcon icono = new ImageIcon(rutaImagen);
                    Image img = icono.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    lblImagen.setIcon(new ImageIcon(img));
                } catch (Exception e) {
                    lblImagen.setIcon(null);
                    lblImagen.setText("Sin imagen");
                }
            } else {
                lblImagen.setIcon(null);
                lblImagen.setText("Sin imagen");
            }
        }
    }
    
    private void agregarCancion() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo de audio");
        FileFilter filtroMP3 = new FileNameExtensionFilter("Archivos MP3", "mp3");
        fileChooser.setFileFilter(filtroMP3);
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivoAudio = fileChooser.getSelectedFile();
            JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
            JTextField txtNombre = new JTextField(archivoAudio.getName().replaceAll("\\.mp3$", ""));
            JTextField txtArtista = new JTextField();
            JTextField txtGenero = new JTextField();
            panel.add(new JLabel("Nombre:"));
            panel.add(txtNombre);
            panel.add(new JLabel("Artista:"));
            panel.add(txtArtista);
            panel.add(new JLabel("Género:"));
            panel.add(txtGenero);
            JButton btnSeleccionarImagen = new JButton("Seleccionar imagen de álbum");
            JLabel lblRutaImagen = new JLabel("No se ha seleccionado imagen");
            panel.add(btnSeleccionarImagen);
            panel.add(lblRutaImagen);
            final String[] rutaImagen = {""};
            btnSeleccionarImagen.addActionListener(evt -> {
                JFileChooser imgChooser = new JFileChooser();
                imgChooser.setDialogTitle("Seleccionar imagen de álbum");
                FileFilter filtroImagenes = new FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "jpeg", "png");
                imgChooser.setFileFilter(filtroImagenes);
                imgChooser.setAcceptAllFileFilterUsed(false);
                if (imgChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    rutaImagen[0] = imgChooser.getSelectedFile().getAbsolutePath();
                    lblRutaImagen.setText(rutaImagen[0]);
                }
            });
            String duracion = obtenerDuracion(archivoAudio);
            boolean datosCompletos = false;
            while (!datosCompletos) {
                int resultado = JOptionPane.showConfirmDialog(this, panel, "Información de la canción", JOptionPane.OK_CANCEL_OPTION);
                if (resultado == JOptionPane.CANCEL_OPTION) {
                    return;
                }
                if (txtNombre.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar un nombre para la canción", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                if (txtArtista.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar el nombre del artista", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                if (txtGenero.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar el género de la canción", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                if (rutaImagen[0].isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe seleccionar una imagen para la canción", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                datosCompletos = true;
            }
            Cancion nuevaCancion = new Cancion(
                txtNombre.getText().trim(),
                txtArtista.getText().trim(),
                duracion,
                rutaImagen[0],
                txtGenero.getText().trim(),
                archivoAudio.getAbsolutePath()
            );
            listaReproduccion.agregarCancion(nuevaCancion);
            actualizarListaCanciones();
            guardarPlaylist();
        }
    }
    
    private void eliminarCancion() {
        int indiceSeleccionado = listaCanciones.getSelectedIndex();
        if (indiceSeleccionado >= 0) {
            if (indiceSeleccionado == cancionActualIndice) {
                detener();
                cancionActualIndice = -1;
            } else if (indiceSeleccionado < cancionActualIndice) {
                cancionActualIndice--;
            }
            listaReproduccion.eliminarCancion(indiceSeleccionado);
            actualizarListaCanciones();
            guardarPlaylist();
        }
    }
    
    private void reproducir() {
        int indiceSeleccionado = listaCanciones.getSelectedIndex();
        if (indiceSeleccionado >= 0) {
            try {
                if (mp3Player != null && cancionActualIndice != indiceSeleccionado) {
                    detener();
                }
                if (isPaused && cancionActualIndice == indiceSeleccionado && mp3Player != null) {
                    mp3Player.resume();
                    isPaused = false;
                    btnPause.setText("Pause");
                } else if (mp3Player == null) {
                    Cancion cancion = listaReproduccion.obtenerCancion(indiceSeleccionado);
                    File archivoAudio = new File(cancion.getRutaArchivo());
                    currentFile = archivoAudio;
                    mp3Player = new MP3Player(archivoAudio);
                    mp3Player.play();
                    cancionActualIndice = indiceSeleccionado;
                    mostrarInfoCancion(cancionActualIndice);
                    btnPause.setEnabled(true);
                    btnStop.setEnabled(true);
                    btnPause.setText("Pause");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al reproducir el archivo: " + e.getMessage(), "Error de reproducción", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void togglePausar() {
        if (mp3Player != null) {
            if (isPaused) {
                mp3Player.resume();
                isPaused = false;
                btnPause.setText("Pause");
            } else {
                mp3Player.pause();
                isPaused = true;
                btnPause.setText("Resume");
            }
        }
    }
    
    private void detener() {
        if (mp3Player != null) {
            mp3Player.close();
            mp3Player = null;
            isPaused = false;
            pauseLocation = 0;
            btnPause.setText("Pause");
            btnPause.setEnabled(false);
            btnStop.setEnabled(false);
        }
    }
    
    private void guardarPlaylist() {
        File archivo = new File(ARCHIVO_PLAYLIST);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            Cancion[] canciones = listaReproduccion.obtenerTodasLasCanciones();
            oos.writeObject(canciones);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar la playlist: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void cargarPlaylist() {
        File archivo = new File(ARCHIVO_PLAYLIST);
        if (archivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                Cancion[] canciones = (Cancion[]) ois.readObject();
                if (mp3Player != null) {
                    mp3Player.close();
                    mp3Player = null;
                }
                cancionActualIndice = -1;
                isPaused = false;
                listaReproduccion = new ListaEnlazada();
                for (Cancion cancion : canciones) {
                    listaReproduccion.agregarCancion(cancion);
                }
                actualizarListaCanciones();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al cargar la playlist: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private String obtenerDuracion(File file) {
        try {
            AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(file);
            Map<?, ?> properties = baseFileFormat.properties();
            Long microseconds = (Long) properties.get("duration");
            int mili = (int) (microseconds / 1000);
            int sec = (mili / 1000) % 60;
            int min = (mili / 1000) / 60;
            return String.format("%02d:%02d", min, sec);
        } catch (Exception e) {
            e.printStackTrace();
            return "Desconocido";
        }
    }
    
    private class MP3Player {
        private AdvancedPlayer player;
        private FileInputStream fis;
        private BufferedInputStream bis;
        private boolean paused;
        private int pausedOnFrame = 0;
        private final File file;
        private Thread playerThread;
        private int currentFramePosition = 0;
        
        public MP3Player(File file) {
            this.file = file;
            this.paused = false;
        }
        
        public void play() {
            play(0);
        }
        
        public void play(long startFrame) {
            try {
                playerThread = new Thread(() -> {
                    try {
                        fis = new FileInputStream(file);
                        bis = new BufferedInputStream(fis);
                        player = new AdvancedPlayer(bis);
                        player.setPlayBackListener(new PlaybackListener() {
                            @Override
                            public void playbackFinished(PlaybackEvent evt) {
                                if (!paused) {
                                    pausedOnFrame = 0;
                                } else {
                                    pausedOnFrame = evt.getFrame();
                                }
                                currentFramePosition = evt.getFrame();
                            }
                        });
                        if (startFrame > 0) {
                            player.play((int) startFrame, Integer.MAX_VALUE);
                        } else {
                            player.play();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                playerThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        public int getCurrentFrame() {
            return currentFramePosition;
        }
        
        public void pause() {
            if (player != null) {
                paused = true;
                close();
            }
        }
        
        public void resume() {
            if (paused) {
                paused = false;
                play(pausedOnFrame);
            }
        }
        
        public void close() {
            try {
                if (player != null) {
                    player.close();
                    player = null;
                }
                if (playerThread != null) {
                    playerThread.interrupt();
                    playerThread = null;
                }
                if (bis != null) {
                    bis.close();
                    bis = null;
                }
                if (fis != null) {
                    fis.close();
                    fis = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
