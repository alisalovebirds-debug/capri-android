
'use strict';

// Temporary Android bridge for Capri's animation-first build.
// Desktop integrations and AI are intentionally disabled.

(() => {
  const defaults = {
    lang: 'en',
    assistant: 'claude',
    motion: 'full',
    motionAsked: true,
    visible: true,
    welcomed: true,
    linked: false,
    claude: false,
    wander: true,
    possess: false,
    rest: 'corner',
    scale: 0.6,
    volume: 0.7,
    muted: false,
    follow: false,
    jealous: true,
    pcReact: false,
    reminders: [],
    memories: [],
    myLines: [],
    userName: '',
    persona: '',
    bond: {}
  };

  let config = { ...defaults };

  try {
    const saved = localStorage.getItem('capri-android-config');
    if (saved) config = { ...config, ...JSON.parse(saved) };
  } catch {}

  const listeners = new Map();

  function on(name, callback) {
    if (!listeners.has(name)) listeners.set(name, new Set());
    listeners.get(name).add(callback);
    return () => listeners.get(name)?.delete(callback);
  }

  function emit(name, value) {
    listeners.get(name)?.forEach((callback) => {
      try { callback(value); } catch (error) {
        console.error('[Capri Android]', error);
      }
    });
  }

  const no = () => {};
  const empty = async () => [];
  const unavailable = async () => null;

  window.host = {
    onClaudeEvent: (fn) => on('claude:event', fn),
    onCommand: (fn) => on('capri:command', fn),
    onConfig: (fn) => on('capri:config', fn),
    onResize: (fn) => on('overlay:resize', fn),
    onWindows: (fn) => on('windows:update', fn),
    onChatDelta: (fn) => on('chat:delta', fn),
    onSetupProgress: (fn) => on('setup:progress', fn),

    getConfig: async () => ({ ...config }),
    setConfig: async (patch) => {
      config = { ...config, ...patch };
      try {
        localStorage.setItem(
          'capri-android-config',
          JSON.stringify(config)
        );
      } catch {}
      emit('capri:config', { ...config });
      return { ...config };
    },

    listSounds: empty,
    cursor: unavailable,
    screens: async () => ({
      current: 'phone',
      origin: { x: 0, y: 0 },
      list: [{
        id: 'phone',
        x: 0,
        y: 0,
        w: innerWidth,
        h: innerHeight
      }]
    }),
    moveScreen: unavailable,
    windowsNow: unavailable,
    hooksStatus: async () => ({ installed: true }),
    chatStatus: async () => ({ available: false }),
    chatMemory: empty,
    setupInfo: async () => ({ available: false }),
    ollamaState: async () => ({ available: false }),
    openApps: empty,
    searchFiles: empty,

    setInteractive: no,
    rearm: no,
    openMenu: no,
    focusOverlay: no,
    chatStop: no,
    chatRemember: no,
    openOllama: no,
    cancelSetup: no,
    keyPage: no,

    chatSend: unavailable,
    lookSnap: unavailable,
    lookSay: unavailable,
    lookWhere: unavailable,
    cpu: unavailable,
    demoWindow: async () => false,
    installHooks: unavailable,
    claudeFound: async () => false,
    windowsNow: unavailable,
    trayPlace: unavailable,
    openClaudeFolder: unavailable,
    copyImage: unavailable,
    installOllama: unavailable,
    startOllama: unavailable,
    pullModel: unavailable,
    useProvider: unavailable,
    launchThroughOllama: unavailable,
    findApp: unavailable,
    closeApp: unavailable,
    findLaunch: unavailable,
    launch: unavailable,
    media: unavailable,
    music: unavailable,
    youtube: unavailable,
    roblox: unavailable,
    openFile: unavailable,
    screenshot: unavailable,
    webSearch: unavailable,
    openSite: unavailable
  };

  console.info('[Capri Android] temporary bridge ready');
})();
