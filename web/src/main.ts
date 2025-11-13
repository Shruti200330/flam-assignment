const img = document.getElementById('frame') as HTMLImageElement;
const stats = document.getElementById('stats') as HTMLDivElement;

function updateStats() {
  const w = img.naturalWidth || img.width;
  const h = img.naturalHeight || img.height;
  const status = w > 0 ? 'Loaded' : 'Loading...';
  stats.innerText = `FPS: N/A | Resolution: ${w}x${h} | Status: ${status}`;
}

img.onload = () => {
  console.log('Frame loaded successfully');
  updateStats();
};

img.onerror = () => {
  console.error('Failed to load frame.png');
  stats.innerText = 'Error: Failed to load frame.png';
};

updateStats();
console.log('Flam Web Viewer initialized');
