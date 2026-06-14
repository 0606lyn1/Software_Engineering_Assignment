<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  CalendarOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  FireOutlined,
  NotificationOutlined,
  RocketOutlined,
  SafetyCertificateOutlined,
  TrophyOutlined,
} from '@ant-design/icons-vue'
import { useVenueStore } from '../stores/venue'
import { api } from '../api'
import type { Announcement } from '../types'

const router = useRouter()
const venueStore = useVenueStore()
const activeChapter = ref(0)
const scrolled = ref(0)
const announcements = ref<Announcement[]>([])
let observer: IntersectionObserver | null = null

const chapters = [
  {
    title: '发现合适场馆',
    label: '目录筛选',
    desc: '按运动类型快速筛选羽毛球、篮球、游泳等资源，场地价格和使用说明集中展示。',
    stat: '6 类',
  },
  {
    title: '避开时间冲突',
    label: '智能提交',
    desc: '提交预约前校验完整时间段，后端负责冲突检测，减少反复沟通和人工登记。',
    stat: '10 秒',
  },
  {
    title: '沉淀使用反馈',
    label: '评论闭环',
    desc: '每个场馆都能查看和发布体验评论，帮助同学判断灯光、地面、设备和服务状态。',
    stat: '实时',
  },
]

const progressStyle = computed(() => ({ width: `${scrolled.value}%` }))
const venueCount = computed(() => venueStore.venues.length)

const handleScroll = () => {
  const max = document.documentElement.scrollHeight - window.innerHeight
  scrolled.value = max > 0 ? Math.min(100, Math.round((window.scrollY / max) * 100)) : 0
}

const scrollToChapter = (index: number) => {
  document.getElementById(`chapter-${index}`)?.scrollIntoView({ behavior: 'smooth', block: 'center' })
}

onMounted(async () => {
  const [announcementRes] = await Promise.all([api.getAnnouncements(), venueStore.fetchVenues()])
  announcements.value = announcementRes.data
  handleScroll()
  window.addEventListener('scroll', handleScroll, { passive: true })

  observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          activeChapter.value = Number((entry.target as HTMLElement).dataset.index || 0)
        }
      })
    },
    { threshold: 0.55 },
  )

  document.querySelectorAll<HTMLElement>('.story-panel').forEach((el) => observer?.observe(el))
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  observer?.disconnect()
})
</script>

<template>
  <div class="scroll-progress"><span :style="progressStyle" /></div>

  <section class="home-hero">
    <div class="home-hero-content reveal-up">
      <a-tag color="red">校园体育资源数字化服务</a-tag>
      <h1>预约、进场、反馈，在一个运动节奏里完成。</h1>
      <p>
        为学生提供规范透明的场馆预约服务，覆盖场馆目录、在线预约、预约记录、
        评论反馈和个人信息维护，让体育资源使用更清晰、更高效。
      </p>
      <a-space wrap>
        <a-button type="primary" size="large" @click="router.push('/reservation')">
          <RocketOutlined />
          立即预约
        </a-button>
        <a-button size="large" @click="router.push('/venues')">查看场馆</a-button>
      </a-space>
    </div>

    <div class="hero-scoreboard reveal-up">
      <div class="score-glow" />
      <div class="score-card primary">
        <span>可预约场馆</span>
        <strong>{{ venueCount }}</strong>
      </div>
      <div class="score-card">
        <ClockCircleOutlined />
        <span>开放时段</span>
        <strong>06:00 - 22:00</strong>
      </div>
      <div class="score-card">
        <TrophyOutlined />
        <span>支持项目</span>
        <strong>羽毛球 / 篮球 / 游泳</strong>
      </div>
    </div>
  </section>

  <section class="campus-service-band">
    <article v-for="item in announcements.slice(0, 3)" :key="item.id">
      <NotificationOutlined />
      <div>
        <strong>{{ item.title }}</strong>
        <span>{{ item.content }}</span>
      </div>
    </article>
  </section>

  <section class="story-section">
    <aside class="story-nav">
      <p>预约流程</p>
      <button
        v-for="(chapter, index) in chapters"
        :key="chapter.title"
        :class="{ active: activeChapter === index }"
        type="button"
        @click="scrollToChapter(index)"
      >
        <span>{{ String(index + 1).padStart(2, '0') }}</span>
        {{ chapter.label }}
      </button>
    </aside>

    <div class="story-track">
      <article
        v-for="(chapter, index) in chapters"
        :id="`chapter-${index}`"
        :key="chapter.title"
        class="story-panel"
        :data-index="index"
      >
        <div class="chapter-index">{{ String(index + 1).padStart(2, '0') }}</div>
        <div>
          <a-tag :color="activeChapter === index ? 'gold' : 'default'">{{ chapter.label }}</a-tag>
          <h2>{{ chapter.title }}</h2>
          <p>{{ chapter.desc }}</p>
          <strong>{{ chapter.stat }}</strong>
        </div>
      </article>
    </div>
  </section>

  <section class="feature-grid">
    <a-card class="feature-card" :bordered="false">
      <CalendarOutlined />
      <h3>在线预约</h3>
      <p>选择场馆和时间段后提交，平台保留完整预约记录。</p>
    </a-card>
    <a-card class="feature-card" :bordered="false">
      <SafetyCertificateOutlined />
      <h3>登录保护</h3>
      <p>预约、个人资料和评论发布均需要登录，保证数据归属明确。</p>
    </a-card>
    <a-card class="feature-card" :bordered="false">
      <FireOutlined />
      <h3>体验反馈</h3>
      <p>围绕每个场馆沉淀评论，帮助后续同学快速判断场地状态。</p>
    </a-card>
    <a-card class="feature-card" :bordered="false">
      <CheckCircleOutlined />
      <h3>个人中心</h3>
      <p>维护用户名、邮箱和角色信息，减少线下登记成本。</p>
    </a-card>
  </section>
</template>
