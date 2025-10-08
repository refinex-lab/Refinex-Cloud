<script lang="ts" setup>
import { onMounted, onUnmounted, ref, computed } from 'vue';
import { getPaletteColorByNumber } from '@sa/color';

defineOptions({ name: 'WaveBg' });

interface Props {
  /** Theme color */
  themeColor: string;
}

const props = defineProps<Props>();

const canvasRef = ref<HTMLCanvasElement | null>(null);
let animationFrameId: number;
let particles: Particle[] = [];

const lightColor = computed(() => getPaletteColorByNumber(props.themeColor, 100));
const mediumColor = computed(() => getPaletteColorByNumber(props.themeColor, 300));
const darkColor = computed(() => getPaletteColorByNumber(props.themeColor, 500));

// 将颜色转换为 RGB 值用于 canvas
const hexToRgb = (hex: string) => {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  return result ? {
    r: parseInt(result[1], 16),
    g: parseInt(result[2], 16),
    b: parseInt(result[3], 16)
  } : { r: 100, g: 149, b: 237 };
};

class Particle {
  x: number;
  y: number;
  size: number;
  speedX: number;
  speedY: number;
  opacity: number;
  canvas: HTMLCanvasElement;

  constructor(canvas: HTMLCanvasElement) {
    this.canvas = canvas;
    this.x = Math.random() * canvas.width;
    this.y = Math.random() * canvas.height;
    this.size = Math.random() * 2.5 + 0.5;
    this.speedX = Math.random() * 0.4 - 0.2;
    this.speedY = Math.random() * 0.4 - 0.2;
    this.opacity = Math.random() * 0.4 + 0.3;
  }

  update() {
    this.x += this.speedX;
    this.y += this.speedY;

    if (this.x > this.canvas.width) this.x = 0;
    if (this.x < 0) this.x = this.canvas.width;
    if (this.y > this.canvas.height) this.y = 0;
    if (this.y < 0) this.y = this.canvas.height;
  }

  draw(ctx: CanvasRenderingContext2D, color: { r: number; g: number; b: number }) {
    ctx.fillStyle = `rgba(${color.r}, ${color.g}, ${color.b}, ${this.opacity})`;
    ctx.beginPath();
    ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
    ctx.fill();
  }
}

const initParticles = (canvas: HTMLCanvasElement) => {
  particles = [];
  const numberOfParticles = Math.floor((canvas.width * canvas.height) / 12000);
  for (let i = 0; i < numberOfParticles; i++) {
    particles.push(new Particle(canvas));
  }
};

const connectParticles = (ctx: CanvasRenderingContext2D, color: { r: number; g: number; b: number }) => {
  for (let i = 0; i < particles.length; i++) {
    for (let j = i + 1; j < particles.length; j++) {
      const dx = particles[i].x - particles[j].x;
      const dy = particles[i].y - particles[j].y;
      const distance = Math.sqrt(dx * dx + dy * dy);

      if (distance < 150) {
        const opacity = (1 - distance / 150) * 0.2;
        ctx.strokeStyle = `rgba(${color.r}, ${color.g}, ${color.b}, ${opacity})`;
        ctx.lineWidth = 0.8;
        ctx.beginPath();
        ctx.moveTo(particles[i].x, particles[i].y);
        ctx.lineTo(particles[j].x, particles[j].y);
        ctx.stroke();
      }
    }
  }
};

const animate = (canvas: HTMLCanvasElement, ctx: CanvasRenderingContext2D) => {
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  const particleColor = hexToRgb(mediumColor.value);
  const lineColor = hexToRgb(darkColor.value);

  particles.forEach(particle => {
    particle.update();
    particle.draw(ctx, particleColor);
  });

  connectParticles(ctx, lineColor);
  animationFrameId = requestAnimationFrame(() => animate(canvas, ctx));
};

const resizeCanvas = (canvas: HTMLCanvasElement, ctx: CanvasRenderingContext2D) => {
  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;
  initParticles(canvas);
};

onMounted(() => {
  const canvas = canvasRef.value;
  if (!canvas) return;

  const ctx = canvas.getContext('2d');
  if (!ctx) return;

  resizeCanvas(canvas, ctx);
  animate(canvas, ctx);

  const handleResize = () => resizeCanvas(canvas, ctx);
  window.addEventListener('resize', handleResize);

  onUnmounted(() => {
    window.removeEventListener('resize', handleResize);
    if (animationFrameId) {
      cancelAnimationFrame(animationFrameId);
    }
  });
});
</script>

<template>
  <div class="absolute-lt z-1 size-full overflow-hidden bg-gradient-to-br from-gray-50 via-white to-blue-50">
    <canvas
      ref="canvasRef"
      class="absolute-lt size-full"
    />
    <div class="absolute-lt size-full bg-white/20 backdrop-blur-[0.5px]" />
  </div>
</template>

<style scoped></style>
